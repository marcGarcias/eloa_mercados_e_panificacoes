package garcias.api.identity.user.domain.exceptions;

import garcias.api.shared.exceptions.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
        super("User not found");
    }
}
