package garcias.api.identity.authentication.domain.exceptions;

import garcias.api.shared.exceptions.UnauthorizedException;

public class InvalidSetupCpfException extends UnauthorizedException {
    public InvalidSetupCpfException() {
        super("CPF de setup inválido.");
    }
}
