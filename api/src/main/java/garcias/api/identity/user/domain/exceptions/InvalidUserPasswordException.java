package garcias.api.identity.user.domain.exceptions;

import garcias.api.shared.exceptions.DomainException;

public class InvalidUserPasswordException extends DomainException {
    public InvalidUserPasswordException() {
        super("A nova senha não pode ser igual à senha atual.");
    }
}

