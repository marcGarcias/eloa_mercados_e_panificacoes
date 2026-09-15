package garcias.api.identity.user.domain.exceptions;

import garcias.api.shared.exceptions.DomainException;

public class InvalidPasswordStrengthException extends DomainException {
    public InvalidPasswordStrengthException() {
        super("A senha deve conter no mínimo 8 caracteres, incluindo pelo menos 1 letra maiúscula, 1 letra minúscula e 1 caractere especial.");
    }
}
