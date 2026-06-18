package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.model.service.UserServiceModel;
import bg.greencom.greencomwebapp.model.user.GreencomUserDetails;
import bg.greencom.greencomwebapp.model.view.*;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public interface UserService {
    void initialize();

    void registerUser(UserServiceModel user, Consumer<Authentication> successfulLoginProcessor);

    UserEntity findUserByEmail(String email);

    UserEntity findUserByUsername(String username);

    void signVoicePlan(VoicePlanViewModel voicePlan, GreencomUserDetails userDetails, byte[] signSignature);

    void signDataPlan(DataPlanViewModel dataPlan, GreencomUserDetails userDetails, byte[] signSignature);

    String unsignPlan(Long contractId, String username, byte[] unsignSignature);

    List<VoicePlanViewModel> getAllVoicePlans(String username);

    List<DataPlanViewModel> getAllDataPlans(String username);

    void signInternetPlan(InternetPlanViewModel internetPlan, GreencomUserDetails userDetails, byte[] signSignature);

    List<InternetPlanViewModel> getAllInternetPlans(String username);

    void signTelevisionPlan(Long planId,Set<Long> additionalPackageIds, GreencomUserDetails userDetails, byte[] signSignature);

    List<TelevisionPlanViewModel> getAllTelevisionPlans(String username);

    UserViewModel getUserInfo(String username);

    BigDecimal redeemLoyaltyPoints(String username, int points);

    boolean isPenaltyRequired(Long id);

    BigDecimal calculatePenalty(Long id);
}
