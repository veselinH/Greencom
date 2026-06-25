package bg.greencom.greencomwebapp.model.entity.enums;

public enum UserRoleEnum {
    ADMIN("Admin"),
    USER("User"),
    MODERATOR("Moderator");

    private final String value;

    UserRoleEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
