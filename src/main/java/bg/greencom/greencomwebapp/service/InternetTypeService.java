package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.InternetTypeEntity;
import bg.greencom.greencomwebapp.model.entity.enums.InternetTypeEnum;

public interface InternetTypeService {
    void initialize();

    InternetTypeEntity findByName(InternetTypeEnum name);
}
