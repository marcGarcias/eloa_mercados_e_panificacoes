package garcias.api.shared.exceptions;

public class SuperAdminModificationNotAllowedException extends DomainException {
    public SuperAdminModificationNotAllowedException() {
        super("Não é permitido alterar ou promover usuários para a função de Proprietário.");
    }
}

