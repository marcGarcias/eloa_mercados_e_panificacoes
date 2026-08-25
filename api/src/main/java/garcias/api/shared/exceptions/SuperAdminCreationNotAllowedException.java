package garcias.api.shared.exceptions;

public class SuperAdminCreationNotAllowedException extends DomainException {
    public SuperAdminCreationNotAllowedException() {
        super("Creating a SUPER_ADMIN user is not allowed through this channel.");
    }
}
