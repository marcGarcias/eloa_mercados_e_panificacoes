package garcias.api.shared.exceptions;

public class AttributeMustBeGreaterThanZeroException extends DomainException {
    public AttributeMustBeGreaterThanZeroException(String attribute, Number value) {
        super(attribute + " must be greater than zero. Received: " + value);
    }
}