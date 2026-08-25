package garcias.api.shared.exceptions;

public class SuperAdminAlreadyExistsException extends ConflictException {
    public SuperAdminAlreadyExistsException() {
        super("System already has a SUPER_ADMIN user. Only one owner is allowed.");
    }
}
