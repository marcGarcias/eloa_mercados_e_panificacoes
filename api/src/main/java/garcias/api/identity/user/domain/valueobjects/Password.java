package garcias.api.identity.user.domain.valueobjects;

import garcias.api.shared.exceptions.AttributeCannotBeEmptyException;

public record Password(String value) {


    public Password(String value) {

        if (value == null || value.isBlank()) {
            throw new AttributeCannotBeEmptyException("Password");
        }

        this.value = value;
    }


    public static Password fromHash(String hash) {

        return new Password(hash);
    }
}