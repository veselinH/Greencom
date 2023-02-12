package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.model.service.UserServiceModel;

public interface UserService {
    void initialize();

    UserServiceModel registerUser(UserServiceModel user);

    UserEntity findUserByEmail(String email);

    UserEntity findUserByUsername(String username);

}
