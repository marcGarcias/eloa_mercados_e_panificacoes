package garcias.api.shared.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import garcias.api.shared.exceptions.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "rate-limit.enabled", havingValue = "true", matchIfMissing = true)
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;
    private final RateLimitProperties properties;
    private final ObjectMapper objectMapper;

    @org.springframework.beans.factory.annotation.Autowired
    public RateLimitFilter(
            RateLimiterService rateLimiterService,
            RateLimitProperties properties,
            @org.springframework.beans.factory.annotation.Autowired(required = false) ObjectMapper objectMapper
    ) {
        this.rateLimiterService = rateLimiterService;
        this.properties = properties;
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper().findAndRegisterModules();
    }

    public RateLimitFilter(
            RateLimiterService rateLimiterService,
            RateLimitProperties properties
    ) {
        this(rateLimiterService, properties, null);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (!properties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        String uri = request.getRequestURI();

        // Isenção explícita de imagens estáticas e actuator/swagger
        if (shouldBypass(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        RateLimitTier tier = resolveTier(uri);
        String clientIp = extractClientIp(request);

        RateLimitResult result = rateLimiterService.check(clientIp, tier);

        if (!result.allowed()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("Retry-After", String.valueOf(result.retryAfterSeconds()));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            ErrorResponse error = new ErrorResponse(
                    HttpStatus.TOO_MANY_REQUESTS.value(),
                    String.format("Limite de requisições excedido. Por favor, aguarde %d segundos antes de tentar novamente.", result.retryAfterSeconds()),
                    request.getRequestURI(),
                    LocalDateTime.now()
            );

            response.getWriter().write(objectMapper.writeValueAsString(error));
            return;
        }

        response.setHeader("X-RateLimit-Limit", String.valueOf(result.limit()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(result.remaining()));

        filterChain.doFilter(request, response);
    }

    private boolean shouldBypass(String uri) {
        return uri.startsWith("/api/storage/images/")
                || uri.startsWith("/actuator")
                || uri.startsWith("/swagger-ui")
                || uri.startsWith("/v3/api-docs");
    }

    private RateLimitTier resolveTier(String uri) {
        if ("/api/auth/login".equals(uri) || "/api/auth/bootstrap".equals(uri)) {
            return RateLimitTier.AUTH;
        }
        if ("/api/auth/refresh".equals(uri)) {
            return RateLimitTier.REFRESH;
        }
        if (uri.startsWith("/api/public/")) {
            return RateLimitTier.PUBLIC;
        }
        if (uri.startsWith("/api/admin/")) {
            return RateLimitTier.ADMIN;
        }
        return RateLimitTier.DEFAULT;
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }
        return request.getRemoteAddr();
    }
}
