package garcias.api.shared.exceptions;

public class SuperAdminModificationNotAllowedException extends DomainException {
    public SuperAdminModificationNotAllowedException() {
        super("Modifying the role to/from SUPER_ADMIN is not allowed.");
    }
}
