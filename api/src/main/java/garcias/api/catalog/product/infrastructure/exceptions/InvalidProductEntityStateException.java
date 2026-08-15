package garcias.api.catalog.product.infrastructure.exceptions;

public class InvalidProductEntityStateException extends RuntimeException {
    public InvalidProductEntityStateException(String message) {
        super(message);
    }
}
