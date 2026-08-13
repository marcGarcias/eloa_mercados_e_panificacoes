package garcias.api.identity.authentication.application.dto.requests;

public record LoginRequest(
        String userCode,
        String password
) {
}