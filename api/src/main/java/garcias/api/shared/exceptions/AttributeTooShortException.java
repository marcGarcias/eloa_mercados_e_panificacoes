package garcias.api.shared.exceptions;

public class AttributeTooShortException extends DomainException {
    public AttributeTooShortException(String attribute, String minCharacters) {
        super(attribute + " must contain at least " + minCharacters + " characters.");
    }
}
