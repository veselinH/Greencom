package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.InternetExtrasEntity;
import bg.greencom.greencomwebapp.model.entity.enums.InternetExtraEnum;

public interface InternetExtraService {
    void initialize();

    InternetExtrasEntity findByName(InternetExtraEnum internetExtraEnum);
}
