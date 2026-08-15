package garcias.api.identity.user.domain.exceptions;

import garcias.api.shared.exceptions.ConflictException;

public class UserAlreadyExistsException
        extends ConflictException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
