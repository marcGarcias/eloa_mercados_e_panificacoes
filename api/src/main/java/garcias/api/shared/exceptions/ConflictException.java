package garcias.api.shared.exceptions;

public abstract class ConflictException
        extends DomainException {

    protected ConflictException(String message) {
        super(message);
    }
}
