package garcias.api.identity.user.domain.valueobjects;

import garcias.api.shared.exceptions.AttributeCannotBeEmptyException;
import garcias.api.shared.exceptions.AttributeTooLongException;

public record UserName(String value) {

    public UserName {

        if (value == null || value.isBlank()) {
            throw new AttributeCannotBeEmptyException("User name");
        }

        value = value.trim();

        if (value.length() > 150) {
            throw new AttributeTooLongException("User name", "150");
        }
    }
}
