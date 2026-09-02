package garcias.api.identity.authentication.domain.exceptions;

import garcias.api.shared.exceptions.UnauthorizedException;

public class InvalidSetupAccessKeyException extends UnauthorizedException {
    public InvalidSetupAccessKeyException(String message) {
        super(message);
    }
}
