package bg.greencom.greencomwebapp.model.exception;

public class ObjectNotFoundException extends RuntimeException {
    private final Long objectId;
    private final String objectType;

    public ObjectNotFoundException(Long planId, String objectType) {
        super("Object type " + objectType + " with ID " + planId + " not found!");
        this.objectId = planId;
        this.objectType = objectType;
    }

    public Long getObjectId() {
        return objectId;
    }

    public String getObjectType() {
        return objectType;
    }
}
