package garcias.api.catalog.product.infrastructure.exceptions;

import garcias.api.shared.exceptions.DomainException;

public class InvalidProductEntityStateException extends DomainException {
    public InvalidProductEntityStateException(String message) {
        super(message);
    }
}
