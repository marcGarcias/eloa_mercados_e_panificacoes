package garcias.api.catalog.product.infrastructure.exceptions;

public class ImageStorageException extends RuntimeException {
    public ImageStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
