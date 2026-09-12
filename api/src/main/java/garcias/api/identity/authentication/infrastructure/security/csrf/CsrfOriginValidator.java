package garcias.api.identity.authentication.infrastructure.security.csrf;

import garcias.api.shared.exceptions.InvalidOriginException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Component
public class CsrfOriginValidator {

    private final List<String> allowedOrigins;

    public CsrfOriginValidator(
            @Value("${app.cors.allowed-origins:http://localhost:4200,https://eloa-mercados-e-panificacoes.vercel.app}") String allowedOriginsConfig
    ) {
        this.allowedOrigins = Arrays.stream(allowedOriginsConfig.split(","))
                .map(String::trim)
                .map(origin -> origin.replaceAll("/+$", "").toLowerCase())
                .filter(origin -> !origin.isEmpty())
                .toList();
    }

    public void validate(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");
        String requestedWith = request.getHeader("X-Requested-With");

        if (origin != null && !origin.isBlank()) {
            if ("null".equalsIgnoreCase(origin.trim())) {
                throw new InvalidOriginException("Origin 'null' não é autorizada.");
            }

            String normalizedOrigin = origin.trim().replaceAll("/+$", "").toLowerCase();
            boolean isAllowed = allowedOrigins.contains(normalizedOrigin);
            if (!isAllowed) {
                throw new InvalidOriginException("Origem não autorizada: " + origin);
            }
            return;
        }

        if (referer != null && !referer.isBlank()) {
            String normalizedRefererOrigin;
            try {
                URI refererUri = URI.create(referer.trim());
                String refererOrigin = refererUri.getScheme() + "://" + refererUri.getHost()
                        + (refererUri.getPort() != -1 ? ":" + refererUri.getPort() : "");
                normalizedRefererOrigin = refererOrigin.toLowerCase();
            } catch (IllegalArgumentException e) {
                throw new InvalidOriginException("Formato de Referer inválido.");
            }

            boolean isAllowed = allowedOrigins.contains(normalizedRefererOrigin);
            if (!isAllowed) {
                throw new InvalidOriginException("Referer não autorizado: " + referer);
            }
            return;
        }

        if (!"XMLHttpRequest".equalsIgnoreCase(requestedWith)) {
            throw new InvalidOriginException("Requisição sem Origin/Referer exige cabeçalho X-Requested-With: XMLHttpRequest.");
        }
    }
}

