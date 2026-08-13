package garcias.api.identity.user.domain.exceptions;

public class UserAlreadyExistsException
        extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
