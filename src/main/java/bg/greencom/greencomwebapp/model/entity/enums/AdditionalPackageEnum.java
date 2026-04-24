package bg.greencom.greencomwebapp.model.entity.enums;

public enum AdditionalPackageEnum {
    SPORT_XTRA("Sport Xtra"),
    MOVIES_XTRA("Movies Xtra"),
    ADULT_XTRA("Adult Xtra");

    private final String value;

    AdditionalPackageEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
