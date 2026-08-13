package garcias.api.identity.authentication.application.dto.responses;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}
