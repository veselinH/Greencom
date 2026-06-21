package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.InternetTypeEntity;
import bg.greencom.greencomwebapp.model.entity.enums.InternetTypeEnum;

/**
 * Manages internet connection technology types (e.g. FTTH, VDSL, cable).
 */
public interface InternetTypeService {

    /** Seeds all {@code InternetTypeEnum} values into the database if not already present. */
    void initialize();

    /** Returns the entity for the given connection technology type. */
    InternetTypeEntity findByName(InternetTypeEnum name);
}
