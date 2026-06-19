package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.MobileExtraEntity;
import bg.greencom.greencomwebapp.model.entity.enums.MobileExtraEnum;

/**
 * Manages optional extras that can be bundled with mobile voice and data plans
 * (e.g. international roaming, voicemail).
 */
public interface MobileExtraService {

    /** Seeds all {@code MobileExtraEnum} values into the database if not already present. */
    void initialize();

    /** Returns the entity for the given mobile extra type. */
    MobileExtraEntity findByName(MobileExtraEnum mobileExtraEnum);
}
