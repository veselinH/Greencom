package bg.greencom.greencomwebapp.model.entity.enums;

public enum TelevisionTypeEnum {
    SATELLITE("Satellite"),
    INTERACTIVE("Interactive");

    private final String value;

    TelevisionTypeEnum(String value) {this.value = value;}

    public String getValue() {return value;}
}
