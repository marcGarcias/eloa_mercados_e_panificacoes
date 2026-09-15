package garcias.api.shared.exceptions;

public class SuperAdminAlreadyExistsException extends ConflictException {
    public SuperAdminAlreadyExistsException() {
        super("Já existe um Proprietário cadastrado no sistema.");
    }
}

