package garcias.api.identity.authentication.domain.exceptions;

import garcias.api.shared.exceptions.ConflictException;

public class BootstrapAlreadyCompletedException
        extends ConflictException {

    public BootstrapAlreadyCompletedException() {
        super("Initial user has already been created.");
    }
}