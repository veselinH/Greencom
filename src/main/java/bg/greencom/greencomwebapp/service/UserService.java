package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.model.service.UserServiceModel;
import bg.greencom.greencomwebapp.model.user.GreencomUserDetails;
import bg.greencom.greencomwebapp.model.view.VoicePlanViewModel;

public interface UserService {
    void initialize();

    UserServiceModel registerUser(UserServiceModel user);

    UserEntity findUserByEmail(String email);

    UserEntity findUserByUsername(String username);

    void addVoicePlan(VoicePlanViewModel voicePlan, GreencomUserDetails userDetails);
}
