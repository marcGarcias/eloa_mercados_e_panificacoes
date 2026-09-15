package garcias.api.identity.authentication.domain.exceptions;

import garcias.api.shared.exceptions.UnauthorizedException;

public class InvalidCredentialsException
        extends UnauthorizedException {

    public InvalidCredentialsException() {
        super("Credenciais inválidas. Verifique seu código de usuário e senha.");
    }
}
