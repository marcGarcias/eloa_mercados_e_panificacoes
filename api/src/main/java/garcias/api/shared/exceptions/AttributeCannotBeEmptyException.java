package garcias.api.shared.exceptions;

public class AttributeCannotBeEmptyException extends DomainException {
    public AttributeCannotBeEmptyException(String emptyAttribute) {
        super(emptyAttribute + " não pode ficar em branco.");
    }
}
