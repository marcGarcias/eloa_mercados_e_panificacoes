package garcias.api.identity.user.domain.valueobjects;

import garcias.api.shared.exceptions.AttributeCannotBeEmptyException;
import garcias.api.shared.exceptions.DomainException;

public record UserCode(String value) {


    public UserCode {

        if (value == null || value.isBlank()) {
            throw new AttributeCannotBeEmptyException("User code");
        }


        if (!value.matches("\\d+")) {
            throw new DomainException("User code must contain only numbers") {};
        }


        if (value.length() < 4) {
            throw new DomainException("User code must have at least 4 digits") {};
        }
    }


    public static UserCode from(Long number) {

        String formatted = String.format("%04d", number);

        return new UserCode(formatted);
    }
}
