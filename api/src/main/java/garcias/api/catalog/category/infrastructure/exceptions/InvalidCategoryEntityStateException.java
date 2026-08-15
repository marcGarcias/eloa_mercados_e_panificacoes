package garcias.api.catalog.category.infrastructure.exceptions;

public class InvalidCategoryEntityStateException extends RuntimeException {
    public InvalidCategoryEntityStateException(String message) {
        super(message);
    }
}
