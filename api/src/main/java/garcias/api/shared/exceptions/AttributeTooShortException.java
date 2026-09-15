package garcias.api.shared.exceptions;

public class AttributeTooShortException extends DomainException {
    public AttributeTooShortException(String attribute, String minCharacters) {
        super(attribute + " deve conter pelo menos " + minCharacters + " caracteres.");
    }
}
