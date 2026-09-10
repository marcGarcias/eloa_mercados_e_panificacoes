package garcias.api.identity.authentication.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Security Handlers (401 / 403) Security Unit Tests")
class SecurityHandlersTest {

    private ObjectMapper objectMapper;
    private CustomAuthenticationEntryPoint authenticationEntryPoint;
    private CustomAccessDeniedHandler accessDeniedHandler;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().findAndRegisterModules();
        authenticationEntryPoint = new CustomAuthenticationEntryPoint(objectMapper);
        accessDeniedHandler = new CustomAccessDeniedHandler(objectMapper);
    }

    @Test
    @DisplayName("CustomAuthenticationEntryPoint should return HTTP 401 Unauthorized with standardized JSON ErrorResponse")
    void shouldReturn401JsonError() throws IOException, jakarta.servlet.ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/admin/catalog");
        MockHttpServletResponse response = new MockHttpServletResponse();

        authenticationEntryPoint.commence(
                request,
                response,
                new InsufficientAuthenticationException("Token missing or expired")
        );

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertTrue(response.getContentType().contains("application/json"));
        String body = response.getContentAsString();
        assertTrue(body.contains("401"));
        assertTrue(body.contains("Unauthorized"));
        assertTrue(body.contains("/api/admin/catalog"));
    }

    @Test
    @DisplayName("CustomAccessDeniedHandler should return HTTP 403 Forbidden with standardized JSON ErrorResponse")
    void shouldReturn403JsonError() throws IOException, jakarta.servlet.ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest("DELETE", "/api/admin/users/0002");
        MockHttpServletResponse response = new MockHttpServletResponse();

        accessDeniedHandler.handle(
                request,
                response,
                new AccessDeniedException("Access denied for current role")
        );

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
        assertTrue(response.getContentType().contains("application/json"));
        String body = response.getContentAsString();
        assertTrue(body.contains("403"));
        assertTrue(body.contains("Forbidden"));
        assertTrue(body.contains("/api/admin/users/0002"));
    }
}
