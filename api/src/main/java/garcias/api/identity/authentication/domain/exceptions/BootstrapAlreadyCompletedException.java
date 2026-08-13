package garcias.api.identity.authentication.domain.exceptions;

public class BootstrapAlreadyCompletedException
        extends RuntimeException {

    public BootstrapAlreadyCompletedException() {
        super("Initial user has already been created.");
    }
}