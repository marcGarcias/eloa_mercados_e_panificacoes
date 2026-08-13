package garcias.api.shared.exceptions;


public abstract class NotFoundException extends DomainException {
    protected NotFoundException(String message) {
        super(message);
    }
}