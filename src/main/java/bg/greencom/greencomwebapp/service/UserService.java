package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.service.UserServiceModel;

public interface UserService {
    void initialize();

    UserServiceModel registerUser(UserServiceModel user);
}
