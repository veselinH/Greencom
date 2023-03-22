package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.model.service.UserServiceModel;
import bg.greencom.greencomwebapp.model.user.GreencomUserDetails;
import bg.greencom.greencomwebapp.model.view.DataPlanViewModel;
import bg.greencom.greencomwebapp.model.view.VoicePlanViewModel;
import org.springframework.security.core.Authentication;

import java.util.function.Consumer;

public interface UserService {
    void initialize();

    void registerUser(UserServiceModel user, Consumer<Authentication> successfulLoginProcessor);

    UserEntity findUserByEmail(String email);

    UserEntity findUserByUsername(String username);

    void addVoicePlan(VoicePlanViewModel voicePlan, GreencomUserDetails userDetails);

    void addDataPlan(DataPlanViewModel dataPlan, GreencomUserDetails userDetails);
}
