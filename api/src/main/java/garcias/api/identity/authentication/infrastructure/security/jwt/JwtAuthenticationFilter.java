package garcias.api.identity.authentication.infrastructure.security.jwt;

import garcias.api.identity.authentication.application.security.AccessTokenManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AccessTokenManager accessTokenManager;

    public JwtAuthenticationFilter(AccessTokenManager accessTokenManager) {
        this.accessTokenManager = accessTokenManager;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        if (!accessTokenManager.isValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String status = accessTokenManager.extractStatus(token);

        if (!"ACTIVE".equals(status)) {
            filterChain.doFilter(request, response);
            return;
        }

        String userCode = accessTokenManager.extractUserCode(token);

        String role = accessTokenManager.extractRole(token);

        var authority = new SimpleGrantedAuthority("ROLE_" + role);

        var authentication = new UsernamePasswordAuthenticationToken(
                userCode,
                null,
                List.of(authority)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
