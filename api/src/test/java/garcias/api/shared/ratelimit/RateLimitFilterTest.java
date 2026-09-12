package garcias.api.shared.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RateLimitFilter Unit Tests")
class RateLimitFilterTest {

    @Mock
    private RateLimiterService rateLimiterService;

    @Mock
    private FilterChain filterChain;

    private RateLimitProperties properties;
    private ObjectMapper objectMapper;
    private RateLimitFilter rateLimitFilter;

    @BeforeEach
    void setUp() {
        properties = new RateLimitProperties();
        properties.setEnabled(true);
        objectMapper = new ObjectMapper().findAndRegisterModules();
        rateLimitFilter = new RateLimitFilter(rateLimiterService, properties, objectMapper);
    }

    @Test
    @DisplayName("Should allow request and set headers when within limit")
    void shouldAllowRequestWithinLimit() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        request.setRemoteAddr("10.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(rateLimiterService.check(eq("10.0.0.1"), eq(RateLimitTier.AUTH)))
                .thenReturn(RateLimitResult.allowed(4, 5));

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertEquals("5", response.getHeader("X-RateLimit-Limit"));
        assertEquals("4", response.getHeader("X-RateLimit-Remaining"));
    }

    @Test
    @DisplayName("Should block request with HTTP 429 and Retry-After header when limit is exceeded")
    void shouldBlockRequestWhenLimitExceeded() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        request.setRemoteAddr("10.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(rateLimiterService.check(eq("10.0.0.1"), eq(RateLimitTier.AUTH)))
                .thenReturn(RateLimitResult.blocked(35, 5));

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(), any());
        assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), response.getStatus());
        assertEquals("35", response.getHeader("Retry-After"));
        assertTrue(response.getContentAsString().contains("429"));
        assertTrue(response.getContentAsString().contains("Limite de requisições excedido"));
    }

    @Test
    @DisplayName("Should bypass static image routes without checking rate limiter")
    void shouldBypassImageRoutes() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/storage/images/sample.webp");
        MockHttpServletResponse response = new MockHttpServletResponse();

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(rateLimiterService);
    }

    @Test
    @DisplayName("Should extract client IP from X-Forwarded-For header when behind proxy")
    void shouldExtractIpFromXForwardedFor() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/public/products");
        request.addHeader("X-Forwarded-For", "203.0.113.195, 70.41.3.18, 150.172.238.178");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(rateLimiterService.check(eq("203.0.113.195"), eq(RateLimitTier.PUBLIC)))
                .thenReturn(RateLimitResult.allowed(59, 60));

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(rateLimiterService).check("203.0.113.195", RateLimitTier.PUBLIC);
        verify(filterChain).doFilter(request, response);
    }
}

