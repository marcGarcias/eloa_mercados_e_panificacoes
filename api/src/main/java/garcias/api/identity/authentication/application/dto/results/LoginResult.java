package garcias.api.identity.authentication.application.dto.results;

public record LoginResult(
        String accessToken,
        String refreshToken
) {
}
