package garcias.api.shared.exceptions;

public abstract class UnauthorizedException extends RuntimeException {
    protected UnauthorizedException(String message) {
        super(message);
    }
}
