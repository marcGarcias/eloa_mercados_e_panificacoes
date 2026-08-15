package garcias.api.identity.authentication.application.dto.results;

public record UserAuthenticationDto(
        String userCode,
        String hashedPassword,
        String role,
        String status
) {}
