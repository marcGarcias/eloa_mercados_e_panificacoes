package garcias.api.shared.exceptions;

public abstract class ForbiddenException extends RuntimeException {
    protected ForbiddenException(String message) {
        super(message);
    }
}
