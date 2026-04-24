package bg.greencom.greencomwebapp.model.entity.enums;

public enum InternetExtraEnum {
    WIFI_ROUTER("Wifi Router"),
    ANTI_VIRUS_PROGRAM("Anti virus program"),
    STATIC_IP("Static IP");

    private final String value;

    InternetExtraEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
