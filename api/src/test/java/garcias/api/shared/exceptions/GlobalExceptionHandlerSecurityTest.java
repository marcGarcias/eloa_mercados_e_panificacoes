package garcias.api.shared.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GlobalExceptionHandler Security Unit Tests")
class GlobalExceptionHandlerSecurityTest {

    private GlobalExceptionHandler exceptionHandler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest("POST", "/api/auth/login");
    }

    @Test
    @DisplayName("Should return HTTP 401 Unauthorized for UnauthorizedException")
    void shouldHandleUnauthorizedException() {
        UnauthorizedException ex = new UnauthorizedException("Invalid credentials") {};

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnauthorizedException(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().status());
        assertEquals("Invalid credentials", response.getBody().message());
        assertEquals("/api/auth/login", response.getBody().path());
    }

    @Test
    @DisplayName("Should return HTTP 403 Forbidden for ForbiddenException and InvalidOriginException (CSRF)")
    void shouldHandleForbiddenException() {
        InvalidOriginException csrfEx = new InvalidOriginException("Origem não autorizada: https://evil.com");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleForbiddenException(csrfEx, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().status());
        assertEquals("Origem não autorizada: https://evil.com", response.getBody().message());
    }

    @Test
    @DisplayName("Should return HTTP 429 Too Many Requests and Retry-After header for RateLimitExceededException")
    void shouldHandleRateLimitExceededException() {
        RateLimitExceededException ex = new RateLimitExceededException(45);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleRateLimitExceededException(ex, request);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertEquals("45", response.getHeaders().getFirst("Retry-After"));
        assertNotNull(response.getBody());
        assertEquals(429, response.getBody().status());
        assertTrue(response.getBody().message().contains("45 segundos"));
    }

    @Test
    @DisplayName("Should sanitize unexpected internal exceptions to prevent information leakage")
    void shouldSanitizeUnexpectedExceptions() {
        Exception rawDatabaseException = new RuntimeException("SELECT * FROM users WHERE password_hash = ... SQL Syntax Error");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnexpectedException(rawDatabaseException, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().status());

        assertEquals("Internal server error.", response.getBody().message());
        assertFalse(response.getBody().message().contains("SELECT"));
        assertFalse(response.getBody().message().contains("password"));
    }
}

