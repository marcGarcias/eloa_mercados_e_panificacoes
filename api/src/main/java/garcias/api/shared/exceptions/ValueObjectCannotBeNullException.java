package garcias.api.shared.exceptions;

import org.springframework.modulith.NamedInterface;

@NamedInterface
public class ValueObjectCannotBeNullException extends DomainException {
    public ValueObjectCannotBeNullException(String valueObjectName) {
        super(valueObjectName + " não pode ser nulo.");
    }
}
