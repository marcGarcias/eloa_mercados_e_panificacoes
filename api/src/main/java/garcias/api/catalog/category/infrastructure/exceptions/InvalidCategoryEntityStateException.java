package garcias.api.catalog.category.infrastructure.exceptions;

import garcias.api.shared.exceptions.DomainException;

public class InvalidCategoryEntityStateException extends DomainException {
    public InvalidCategoryEntityStateException(String message) {
        super(message);
    }
}
