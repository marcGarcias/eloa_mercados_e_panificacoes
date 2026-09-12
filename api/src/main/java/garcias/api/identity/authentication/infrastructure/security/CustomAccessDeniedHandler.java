package garcias.api.identity.authentication.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import garcias.api.shared.exceptions.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler() {
        this.objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper().findAndRegisterModules();
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ErrorResponse error = new ErrorResponse(
                HttpServletResponse.SC_FORBIDDEN,
                "Forbidden: You do not have permission to access this resource.",
                request.getRequestURI(),
                LocalDateTime.now()
        );

        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}

