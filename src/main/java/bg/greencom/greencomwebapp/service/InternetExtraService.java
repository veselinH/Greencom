package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.InternetExtrasEntity;
import bg.greencom.greencomwebapp.model.entity.enums.InternetExtraEnum;

/**
 * Manages optional extras that can be added to an internet plan
 * (e.g. static IP, router rental).
 */
public interface InternetExtraService {

    /** Seeds all {@code InternetExtraEnum} values into the database if not already present. */
    void initialize();

    /** Returns the entity for the given internet-extra type. */
    InternetExtrasEntity findByName(InternetExtraEnum internetExtraEnum);
}
