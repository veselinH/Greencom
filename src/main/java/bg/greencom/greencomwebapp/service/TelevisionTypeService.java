package bg.greencom.greencomwebapp.service;

/**
 * Manages television signal delivery types (e.g. cable, satellite, IPTV).
 */
public interface TelevisionTypeService {

    /** Seeds all {@code TelevisionTypeEnum} values into the database if not already present. */
    void initialize();
}
