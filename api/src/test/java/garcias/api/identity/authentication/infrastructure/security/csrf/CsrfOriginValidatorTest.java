package garcias.api.identity.authentication.infrastructure.security.csrf;

import garcias.api.shared.exceptions.InvalidOriginException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CsrfOriginValidator Security Unit Tests")
class CsrfOriginValidatorTest {

    private final String allowedOrigins = "http://localhost:4200,https://eloa-mercados-e-panificacoes.vercel.app";
    private CsrfOriginValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CsrfOriginValidator(allowedOrigins);
    }

    @Test
    @DisplayName("Should allow request with authorized Origin header")
    void shouldAllowAuthorizedOrigin() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Origin", "http://localhost:4200");

        assertDoesNotThrow(() -> validator.validate(request));
    }

    @Test
    @DisplayName("Should allow request with authorized Origin containing trailing slash")
    void shouldAllowAuthorizedOriginWithTrailingSlash() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Origin", "https://eloa-mercados-e-panificacoes.vercel.app/");

        assertDoesNotThrow(() -> validator.validate(request));
    }

    @Test
    @DisplayName("Should throw InvalidOriginException when Origin is 'null'")
    void shouldRejectNullOrigin() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Origin", "null");

        InvalidOriginException exception = assertThrows(InvalidOriginException.class, () -> validator.validate(request));
        assertTrue(exception.getMessage().contains("Origin 'null'"));
    }

    @Test
    @DisplayName("Should throw InvalidOriginException when Origin is unauthorized or malicious")
    void shouldRejectUnauthorizedOrigin() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Origin", "https://malicious-site.com");

        InvalidOriginException exception = assertThrows(InvalidOriginException.class, () -> validator.validate(request));
        assertTrue(exception.getMessage().contains("Origem não autorizada"));
    }

    @Test
    @DisplayName("Should allow request when Origin is absent but Referer is authorized")
    void shouldAllowAuthorizedRefererWhenOriginAbsent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Referer", "http://localhost:4200/admin/catalog");

        assertDoesNotThrow(() -> validator.validate(request));
    }

    @Test
    @DisplayName("Should throw InvalidOriginException when Referer is unauthorized")
    void shouldRejectUnauthorizedReferer() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Referer", "https://attacker.com/page");

        InvalidOriginException exception = assertThrows(InvalidOriginException.class, () -> validator.validate(request));
        assertTrue(exception.getMessage().contains("Referer não autorizado"));
    }

    @Test
    @DisplayName("Should throw InvalidOriginException when Referer has invalid URI format")
    void shouldRejectMalformedReferer() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Referer", ":::not-a-valid-uri");

        InvalidOriginException exception = assertThrows(InvalidOriginException.class, () -> validator.validate(request));
        assertTrue(exception.getMessage().contains("Formato de Referer inválido"));
    }

    @Test
    @DisplayName("Should allow request when Origin and Referer are absent but X-Requested-With is XMLHttpRequest")
    void shouldAllowWhenXRequestedWithIsPresent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Requested-With", "XMLHttpRequest");

        assertDoesNotThrow(() -> validator.validate(request));
    }

    @Test
    @DisplayName("Should throw InvalidOriginException when Origin, Referer and X-Requested-With are all absent")
    void shouldRejectWhenAllHeadersAbsent() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        InvalidOriginException exception = assertThrows(InvalidOriginException.class, () -> validator.validate(request));
        assertTrue(exception.getMessage().contains("exige cabeçalho X-Requested-With"));
    }
}

