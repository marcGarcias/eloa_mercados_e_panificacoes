package garcias.api.identity.authentication.domain.exceptions;

import garcias.api.shared.exceptions.UnauthorizedException;

public class MissingRefreshTokenException extends UnauthorizedException {
    public MissingRefreshTokenException() {
        super("Refresh token is missing.");
    }
}
