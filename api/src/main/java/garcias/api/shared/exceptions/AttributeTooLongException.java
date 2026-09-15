package garcias.api.shared.exceptions;

public class AttributeTooLongException extends DomainException {
    public AttributeTooLongException(String attribute, String characterLimit) {
        super(attribute + " não pode exceder " + characterLimit + " caracteres.");
    }
}
