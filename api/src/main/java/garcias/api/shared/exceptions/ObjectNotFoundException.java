package garcias.api.shared.exceptions;


public class ObjectNotFoundException extends NotFoundException {
    public ObjectNotFoundException(Long id) {
        super("Object with id " + id + " not found.");
    }
}