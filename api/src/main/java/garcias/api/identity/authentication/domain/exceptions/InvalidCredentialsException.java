package garcias.api.identity.authentication.domain.exceptions;

public class InvalidCredentialsException
        extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid credentials");
    }
}