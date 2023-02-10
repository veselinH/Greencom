package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.MobileExtraEntity;
import bg.greencom.greencomwebapp.model.entity.enums.MobileExtraEnum;

public interface MobileExtraService {
    void initialize();

    MobileExtraEntity findByName(MobileExtraEnum mobileExtraEnum);
}
