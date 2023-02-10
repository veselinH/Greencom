package bg.greencom.greencomwebapp.model.entity.enums;

public enum MobileExtraEnum {

    MOBILE_TV("Mobile TV"),
    MOBILE_MUSIC("Mobile Music"),
    GREENCOM_PREMIUM_CARD("Greencom Premium Card");

    private final String value;


    MobileExtraEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
