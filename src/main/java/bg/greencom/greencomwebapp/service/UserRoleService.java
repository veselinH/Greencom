package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.UserRoleEntity;
import bg.greencom.greencomwebapp.model.entity.enums.UserRoleEnum;

public interface UserRoleService {
    void initialize();

    UserRoleEntity findByName(UserRoleEnum userRoleEnum);
}
