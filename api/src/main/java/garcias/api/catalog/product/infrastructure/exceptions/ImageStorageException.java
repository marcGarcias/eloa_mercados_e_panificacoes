package garcias.api.catalog.product.infrastructure.exceptions;

import garcias.api.shared.exceptions.InternalServerException;

public class ImageStorageException extends InternalServerException {
    public ImageStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
