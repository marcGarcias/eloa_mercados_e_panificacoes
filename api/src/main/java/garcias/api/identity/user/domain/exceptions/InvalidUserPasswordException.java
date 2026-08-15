package garcias.api.identity.user.domain.exceptions;

import garcias.api.shared.exceptions.DomainException;

public class InvalidUserPasswordException extends DomainException {
    public InvalidUserPasswordException() {
        super("New password cannot be the same as current password");
    }
}
