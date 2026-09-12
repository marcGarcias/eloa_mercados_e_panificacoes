package garcias.api.identity.authentication.infrastructure.security.jwt;

import garcias.api.identity.authentication.application.security.AccessTokenManager;
import garcias.api.identity.authentication.infrastructure.security.CustomAuthenticationEntryPoint;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter Security Unit Tests")
class JwtAuthenticationFilterTest {

    @Mock
    private AccessTokenManager accessTokenManager;

    @Mock
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        jwtAuthenticationFilter = new JwtAuthenticationFilter(accessTokenManager, authenticationEntryPoint);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should authenticate request and populate SecurityContextHolder when valid Bearer token is provided")
    void shouldAuthenticateValidBearerToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/admin/products");
        request.addHeader("Authorization", "Bearer valid.access.token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(accessTokenManager.isValid("valid.access.token")).thenReturn(true);
        when(accessTokenManager.extractStatus("valid.access.token")).thenReturn("ACTIVE");
        when(accessTokenManager.extractUserCode("valid.access.token")).thenReturn("0001");
        when(accessTokenManager.extractRole("valid.access.token")).thenReturn("ADMIN");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("0001", auth.getPrincipal());
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(authenticationEntryPoint);
    }

    @Test
    @DisplayName("Should pass request through without authentication when Authorization header is absent")
    void shouldPassWithoutAuthWhenNoHeader() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/public/products");
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(accessTokenManager, authenticationEntryPoint);
    }

    @Test
    @DisplayName("Should invoke authenticationEntryPoint and halt chain when token is invalid or expired")
    void shouldRejectInvalidOrExpiredToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/admin/products");
        request.addHeader("Authorization", "Bearer expired.or.invalid.token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(accessTokenManager.isValid("expired.or.invalid.token")).thenReturn(false);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(authenticationEntryPoint).commence(eq(request), eq(response), any());
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("Should invoke authenticationEntryPoint and halt chain when user status in token is not ACTIVE")
    void shouldRejectWhenUserStatusIsNotActive() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/admin/products");
        request.addHeader("Authorization", "Bearer token.with.inactive.user");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(accessTokenManager.isValid("token.with.inactive.user")).thenReturn(true);
        when(accessTokenManager.extractStatus("token.with.inactive.user")).thenReturn("SUSPENDED");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(authenticationEntryPoint).commence(eq(request), eq(response), any());
        verify(filterChain, never()).doFilter(any(), any());
    }
}

