package garcias.api.identity.authentication.infrastructure.security.exceptions;

import garcias.api.shared.exceptions.InternalServerException;

public class TokenGenerationException extends InternalServerException {
    public TokenGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
