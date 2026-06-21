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

/**
 * Core user-management service: registration, authentication, plan sign/unsign,
 * loyalty-point redemption, and profile data assembly.
 */
public interface UserService {

    /** Seeds the default admin account if no users exist yet. */
    void initialize();

    /**
     * Creates a new user account and immediately authenticates the session via the
     * supplied {@code successfulLoginProcessor} consumer (auto-login after register).
     */
    void registerUser(UserServiceModel user, Consumer<Authentication> successfulLoginProcessor);

    /** Looks up a user by email address. Returns {@code null} if not found. */
    UserEntity findUserByEmail(String email);

    /** Looks up a user by username. Returns {@code null} if not found. */
    UserEntity findUserByUsername(String username);

    /** Subscribes the user to a voice plan, updates the monthly debt, and persists the contract. */
    void signVoicePlan(VoicePlanViewModel voicePlan, GreencomUserDetails userDetails, byte[] signSignature);

    /** Subscribes the user to a data plan, updates the monthly debt, and persists the contract. */
    void signDataPlan(DataPlanViewModel dataPlan, GreencomUserDetails userDetails, byte[] signSignature);

    /**
     * Terminates a contract, reduces the user's monthly debt by the plan price
     * (and any add-on prices), and returns the plan name for the success message.
     */
    String unsignPlan(Long contractId, String username, byte[] unsignSignature);

    /** Returns all active voice-plan contracts for the user as view models. */
    List<VoicePlanViewModel> getAllVoicePlans(String username);

    /** Returns all active data-plan contracts for the user as view models. */
    List<DataPlanViewModel> getAllDataPlans(String username);

    /** Subscribes the user to an internet plan, updates the monthly debt, and persists the contract. */
    void signInternetPlan(InternetPlanViewModel internetPlan, GreencomUserDetails userDetails, byte[] signSignature);

    /** Returns all active internet-plan contracts for the user as view models. */
    List<InternetPlanViewModel> getAllInternetPlans(String username);

    /**
     * Subscribes the user to a television plan with the chosen additional packages,
     * updates the monthly debt to include all package prices, and persists the contract.
     */
    void signTelevisionPlan(Long planId, Set<Long> additionalPackageIds,
                            GreencomUserDetails userDetails, byte[] signSignature);

    /** Returns all active television-plan contracts for the user, including add-on package details. */
    List<TelevisionPlanViewModel> getAllTelevisionPlans(String username);

    /**
     * Builds a profile view model including loyalty balance and tier.
     * Loyalty data is fetched best-effort — the profile loads even when the
     * loyalty-service is unreachable (tier shown as "—", points as 0).
     */
    UserViewModel getUserInfo(String username);

    /**
     * Redeems the given number of loyalty points for a BGN discount applied to the
     * user's monthly bill. Throws {@code LoyaltyException} if the balance is
     * insufficient or the loyalty-service is unavailable.
     *
     * @return the discount amount in BGN that was deducted from the monthly debt
     */
    BigDecimal redeemLoyaltyPoints(String username, int points);

    /**
     * Returns {@code true} if the contract is still within its minimum term and
     * an early termination penalty applies to unsigning.
     */
    boolean isPenaltyRequired(Long id);

    /**
     * Calculates the early termination penalty in BGN.
     * The penalty covers the remaining months in the minimum term, capped at 3 months' price.
     */
    BigDecimal calculatePenalty(Long id);
}
