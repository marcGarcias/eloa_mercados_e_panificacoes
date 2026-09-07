package garcias.api.shared.exceptions;

public class InvalidOriginException extends ForbiddenException {
    public InvalidOriginException(String message) {
        super(message);
    }
}
