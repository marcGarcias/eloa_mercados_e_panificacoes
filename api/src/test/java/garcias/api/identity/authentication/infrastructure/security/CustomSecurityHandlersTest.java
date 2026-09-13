package garcias.api.identity.authentication.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes Unitários dos Handlers de Segurança Customizados")
class CustomSecurityHandlersTest {

    @Test
    @DisplayName("CustomAccessDeniedHandler deve responder com 403 Forbidden e JSON")
    void shouldHandleAccessDenied() throws Exception {
        CustomAccessDeniedHandler handler = new CustomAccessDeniedHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/admin/users");
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.handle(request, response, new AccessDeniedException("Acesso negado"));

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentType()).contains("application/json");
        assertThat(response.getContentAsString()).contains("Forbidden");
        assertThat(response.getContentAsString()).contains("/api/admin/users");
    }

    @Test
    @DisplayName("CustomAuthenticationEntryPoint deve responder com 401 Unauthorized e JSON")
    void shouldHandleAuthenticationEntryPoint() throws Exception {
        CustomAuthenticationEntryPoint entryPoint = new CustomAuthenticationEntryPoint();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/admin/products");
        MockHttpServletResponse response = new MockHttpServletResponse();

        entryPoint.commence(request, response, new BadCredentialsException("Token inválido"));

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).contains("application/json");
        assertThat(response.getContentAsString()).contains("Unauthorized");
        assertThat(response.getContentAsString()).contains("/api/admin/products");
    }
}
