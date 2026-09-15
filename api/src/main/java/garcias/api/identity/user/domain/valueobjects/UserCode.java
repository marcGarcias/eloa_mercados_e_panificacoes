package garcias.api.identity.user.domain.valueobjects;

import garcias.api.shared.exceptions.AttributeCannotBeEmptyException;
import garcias.api.shared.exceptions.DomainException;

public record UserCode(String value) {

    public UserCode {

        if (value == null || value.isBlank()) {
            throw new AttributeCannotBeEmptyException("Código de usuário");
        }

        if (!value.matches("\\d+")) {
            throw new DomainException("O código de usuário deve conter apenas números.") {};
        }

        if (value.length() < 4) {
            throw new DomainException("O código de usuário deve conter pelo menos 4 dígitos.") {};
        }
    }

    public static UserCode from(Long number) {

        String formatted = String.format("%04d", number);

        return new UserCode(formatted);
    }
}

