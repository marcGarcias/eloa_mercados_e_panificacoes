package garcias.api.shared.exceptions;


public class ObjectAlreadyExistsException extends ConflictException {
    public ObjectAlreadyExistsException(String objectName, String value) {super(objectName + " already exists: " + value);}
}