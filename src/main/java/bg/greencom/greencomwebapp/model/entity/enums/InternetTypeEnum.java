package bg.greencom.greencomwebapp.model.entity.enums;

public enum InternetTypeEnum {
    FIBER_NET("FiberNet"),
    VDSL("VDSL");

    private final String value;

    InternetTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
