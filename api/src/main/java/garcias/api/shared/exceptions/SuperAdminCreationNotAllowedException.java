package garcias.api.shared.exceptions;

public class SuperAdminCreationNotAllowedException extends DomainException {
    public SuperAdminCreationNotAllowedException() {
        super("Não é permitido criar usuários com perfil de Proprietário.");
    }
}

