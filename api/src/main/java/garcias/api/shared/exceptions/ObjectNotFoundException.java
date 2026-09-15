package garcias.api.shared.exceptions;

public class ObjectNotFoundException extends NotFoundException {
    public ObjectNotFoundException(Long id) {
        super("Registro não encontrado.");
    }

    public ObjectNotFoundException() {
        super("Registro não encontrado.");
    }
}
