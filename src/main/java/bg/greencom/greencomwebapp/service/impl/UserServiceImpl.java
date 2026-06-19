package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.client.LoyaltyFacade;
import bg.greencom.greencomwebapp.client.dto.LoyaltyResponse;
import bg.greencom.greencomwebapp.model.entity.*;
import bg.greencom.greencomwebapp.model.entity.enums.UserRoleEnum;
import bg.greencom.greencomwebapp.model.service.UserServiceModel;
import bg.greencom.greencomwebapp.model.user.GreencomUserDetails;
import bg.greencom.greencomwebapp.model.view.*;
import bg.greencom.greencomwebapp.repository.TelevisionPlanRepository;
import bg.greencom.greencomwebapp.repository.UserRepository;
import bg.greencom.greencomwebapp.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Consumer;

/**
 * Service implementation responsible for processing user-related business operations
 * such as authentication, profile management, and signing/unsigning service plans.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRoleService userRoleService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final VoicePlanService voicePlanService;
    private final DataPlanService dataPlanService;
    private final PlanService planService;
    private final ContractService contractService;
    private final InternetPlanService internetPlanService;
    private final TelevisionPlanService televisionPlanService;
    private final AdditionalPackageService additionalPackageService;
    private final LoyaltyFacade loyaltyFacade;

    public UserServiceImpl(UserRoleService userRoleService, UserRepository userRepository, ModelMapper modelMapper, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, VoicePlanService voicePlanService, DataPlanService dataPlanService, PlanService planService, ContractService contractService, InternetPlanService internetPlanService, TelevisionPlanService televisionPlanService, AdditionalPackageService additionalPackageService, LoyaltyFacade loyaltyFacade) {
        this.userRoleService = userRoleService;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.voicePlanService = voicePlanService;
        this.dataPlanService = dataPlanService;
        this.planService = planService;
        this.contractService = contractService;
        this.internetPlanService = internetPlanService;
        this.televisionPlanService = televisionPlanService;
        this.additionalPackageService = additionalPackageService;
        this.loyaltyFacade = loyaltyFacade;
    }

    /**
     * Seeds the initial administrator profile into the system if no users exist.
     */
    @Override
    public void initialize() {
        if (userRepository.count() == 0) {
            UserEntity user = new UserEntity();
            user.getRoles().add(userRoleService.findByName(UserRoleEnum.ADMIN));
            user.getRoles().add(userRoleService.findByName(UserRoleEnum.USER));

            user
                    .setUsername("admin")
                    .setFirstName("Veselin")
                    .setLastName("Hristov")
                    .setEmail("admin@greencom.bg")
                    .setTotalDebtPerMonth(BigDecimal.ZERO)
                    .setPassword(passwordEncoder.encode("admin"))
                    .setRegisteredOn(LocalDateTime.now());

            userRepository.save(user);
        }
    }

    /**
     * Registers a new client in the database and automatically triggers a successful security login session.
     *
     * @param userServiceModel         DTO containing user credentials and registration details.
     * @param successfulLoginProcessor Consumer callback used to process the post-registration login token.
     */
    @Override
    public void registerUser(UserServiceModel userServiceModel,
                                         Consumer<Authentication> successfulLoginProcessor) {

        UserEntity user = modelMapper.map(userServiceModel, UserEntity.class);
        UserRoleEntity userRole = userRoleService.findByName(UserRoleEnum.USER);

        user.setPassword(passwordEncoder.encode(userServiceModel.getPassword()));
        user.setRegisteredOn(LocalDateTime.now());
        user.getRoles().add(userRole);
        user.setTotalDebtPerMonth(BigDecimal.ZERO);

        userRepository.save(user);

//      Automatically login the user after registering an account
        UserDetails userDetails = userDetailsService.loadUserByUsername(userServiceModel.getUsername());

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );

        successfulLoginProcessor.accept(auth);
    }

    @Override
    public UserEntity findUserByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .orElse(null);
    }

    @Override
    public UserEntity findUserByUsername(String username) {

        return userRepository
                .findByUsername(username)
                .orElse(null);
    }

    /**
     * Links a mobile voice plan to the user profile, recalculates monthly debt, and creates a contract record.
     */
    @Override
    public void signVoicePlan(VoicePlanViewModel voicePlan, GreencomUserDetails userDetails, byte[] signSignature) {

        UserEntity user = this.findUserByUsername(userDetails.getUsername());
        VoicePlanEntity voicePlanFromDB = voicePlanService.findEntityById(voicePlan.getId());

        user.getUserVoiceMobilePlans().add(voicePlanFromDB);

        // Increase monthly recurring debt by the plan price
        BigDecimal totalDebt = user.getTotalDebtPerMonth();
        totalDebt = totalDebt.add(voicePlanFromDB.getPrice());
        user.setTotalDebtPerMonth(totalDebt);

        userRepository.saveAndFlush(user);

        contractService.addContract(voicePlanFromDB, user,null,  signSignature);

    }

    /**
     * Links a mobile data plan to the user profile, recalculates monthly debt, and creates a contract record.
     */
    @Override
    public void signDataPlan(DataPlanViewModel dataPlan, GreencomUserDetails userDetails, byte[] signSignature) {

        UserEntity user = this.findUserByUsername(userDetails.getUsername());
        DataPlanEntity dataPlanFromDB = dataPlanService.findEntityById(dataPlan.getId());

        user.getUserDataPlans().add(dataPlanFromDB);

        // Increase monthly recurring debt by the plan price
        BigDecimal totalDebt = user.getTotalDebtPerMonth();
        totalDebt = totalDebt.add(dataPlanFromDB.getPrice());
        user.setTotalDebtPerMonth(totalDebt);

        userRepository.saveAndFlush(user);

        contractService.addContract(dataPlanFromDB, user, null, signSignature);
    }

    /**
     * Terminates an existing active plan subscription, handles dynamic debt deductions for base plans
     * plus attached add-on packages, and prevents debt metrics from falling into negative fields.
     *
     * @param contractId       The target subscription contract identifier.
     * @param username         The owner's system login name.
     * @param unsignSignature  Digital signature payload finalizing cancellation.
     * @return                 The descriptive name of the cancelled subscription target.
     */
    @Override
    @Transactional
    public String unsignPlan(Long contractId, String username, byte[] unsignSignature) {

        StringBuilder planName = new StringBuilder();

        UserEntity user = findUserByUsername(username);
        ContractViewModel userContract = contractService.findById(contractId);
        PlanViewModel userPlan = planService.findPlanById(userContract.getPlanId());

        // Deduct base plan price from monthly debt balance
        BigDecimal totalDebt = user.getTotalDebtPerMonth();
        totalDebt = totalDebt.subtract(userPlan.getPrice());

        // Deduct recurring prices of any associated add-on packages
        if (userContract.getAdditionalPackageViewModels() != null) {
            for (AdditionalPackageViewModel additionalPackage : userContract.getAdditionalPackageViewModels()){
                totalDebt = totalDebt.subtract(additionalPackage.getPrice());
            }
        }

        user.setTotalDebtPerMonth(totalDebt);

        // Safety check to ensure calculation roundoffs never drop debt balances below zero
        if (user.getTotalDebtPerMonth().compareTo(BigDecimal.ZERO) < 0) {
            user.setTotalDebtPerMonth(BigDecimal.ZERO);
        }

        userRepository.saveAndFlush(user);

        contractService.deactivateContract(contractId, unsignSignature);

        planName.append(userPlan.getName());
        return planName.toString();

    }

    /**
     * Retrieves all active mobile voice plan subscriptions belonging to the specified user.
     * Maps database contracts directly to view models to maintain unique contract references.
     */
    @Override
    @Transactional(readOnly = true)
    public List<VoicePlanViewModel> getAllVoicePlans(String username) {

        UserEntity user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return Collections.emptyList();

        // Map from Contract to ViewModels to preserve the unique Contract ID
        return user.getUserContracts().stream()
                .filter(ContractEntity::isActive)
                .filter(contract -> contract.getPlan() instanceof VoicePlanEntity)
                .map(contract -> {
                    VoicePlanViewModel viewModel = modelMapper.map(contract.getPlan(), VoicePlanViewModel.class);
                    viewModel.setContractId(contract.getId()); // Explicitly set the unique ID
                    return viewModel;
                })
                .toList();
    }

    /**
     * Retrieves all active mobile data plan subscriptions belonging to the specified user.
     * Maps database contracts directly to view models to maintain unique contract references.
     */
    @Override
    @Transactional(readOnly = true)
    public List<DataPlanViewModel> getAllDataPlans(String username) {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return Collections.emptyList();

        // Map from Contract to ViewModels to preserve the unique Contract ID
        return user.getUserContracts().stream()
                .filter(ContractEntity::isActive)
                .filter(contract -> contract.getPlan() instanceof DataPlanEntity)
                .map(contract -> {
                    DataPlanViewModel viewModel = modelMapper.map(contract.getPlan(), DataPlanViewModel.class);
                    viewModel.setContractId(contract.getId()); // Explicitly set the unique ID
                    return viewModel;
                })
                .toList();
    }

    /**
     * Links an internet plan to the user profile, recalculates monthly debt, and creates a contract record.
     */
    @Override
    public void signInternetPlan(InternetPlanViewModel internetPlan, GreencomUserDetails userDetails, byte[] signSignature) {

        UserEntity user = this.findUserByUsername(userDetails.getUsername());
        InternetPlanEntity internetPlanFromDB = internetPlanService.findEntityById(internetPlan.getId());

        user.getUserInternetPlans().add(internetPlanFromDB);

        // Increase monthly recurring debt by the plan price
        BigDecimal totalDebt = user.getTotalDebtPerMonth();
        totalDebt = totalDebt.add(internetPlanFromDB.getPrice());
        user.setTotalDebtPerMonth(totalDebt);

        userRepository.saveAndFlush(user);

        contractService.addContract(internetPlanFromDB, user, null, signSignature);
    }

    /**
     * Retrieves all active internet subscription plans for a specific user profile.
     * Explicitly maps internal type structures and binds persistent contract identifiers.
     */
    @Override
    @Transactional(readOnly = true)
    public List<InternetPlanViewModel> getAllInternetPlans(String username) {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return Collections.emptyList();

        // Map from Contract to ViewModels to preserve the unique Contract ID
        return user.getUserContracts().stream()
                .filter(ContractEntity::isActive)
                .filter(contract -> contract.getPlan() instanceof InternetPlanEntity)
                .map(contract -> {
                    InternetPlanViewModel viewModel = modelMapper.map(contract.getPlan(), InternetPlanViewModel.class);
                    viewModel.setInternetType(((InternetPlanEntity) contract.getPlan()).getInternetType().getName().getValue());
                    viewModel.setContractId(contract.getId()); // Explicitly set the unique ID
                    return viewModel;
                })
                .toList();
    }

    /**
     * Provisions a television subscription package containing a base plan and optional extra channels,
     * adding combined recurring fees to the client's financial profile.
     */
    @Override
    public void signTelevisionPlan(Long planId, Set<Long> additionalPackageIds, GreencomUserDetails userDetails, byte[] signSignature) {

        UserEntity user = this.findUserByUsername(userDetails.getUsername());
        TelevisionPlanEntity televisionPlanFromDB = televisionPlanService.findEntityById(planId);
        Set<AdditionalPackageEntity> additionalPackagesFromDB = additionalPackageService.findAllByIds(additionalPackageIds);

        user.getUserTelevisionPlans().add(televisionPlanFromDB);

        // Calculate cumulative debt addition (Base plan + all supplementary add-ons)
        BigDecimal totalDebt = user.getTotalDebtPerMonth();
        totalDebt = totalDebt.add(televisionPlanFromDB.getPrice());
        for (AdditionalPackageEntity packageEntity : additionalPackagesFromDB) {
            totalDebt = totalDebt.add(packageEntity.getPrice());
        }
        user.setTotalDebtPerMonth(totalDebt);

        userRepository.saveAndFlush(user);

        contractService.addContract(televisionPlanFromDB, user, additionalPackagesFromDB, signSignature);
    }

    /**
     * Fetches active television subscriptions for a user, flattening supplementary add-on data
     * and calculating aggregated total pricing directly within the returned view objects.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TelevisionPlanViewModel> getAllTelevisionPlans(String username) {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return Collections.emptyList();

        // Map from Contract to ViewModels to preserve the unique Contract ID
        return user.getUserContracts().stream()
                .filter(ContractEntity::isActive)
                .filter(contract -> contract.getPlan() instanceof TelevisionPlanEntity)
                .map(contract -> {
                    TelevisionPlanViewModel viewModel = modelMapper.map(contract.getPlan(), TelevisionPlanViewModel.class);
                    viewModel.setTelevisionType(((TelevisionPlanEntity) contract.getPlan()).getTelevisionType().getName().getValue());
                    viewModel.setContractId(contract.getId()); // Explicitly set the unique ID
                    if (contract.getAdditionalPackageEntities() != null) {
                        for (AdditionalPackageEntity additionalPackage : contract.getAdditionalPackageEntities()) {
                            AdditionalPackageViewModel additionalPackageViewModel = modelMapper.map(additionalPackage, AdditionalPackageViewModel.class);
                            additionalPackageViewModel.setName(additionalPackage.getName().getValue());
                            viewModel.getAdditionalPackages().add(additionalPackageViewModel);

                            // Dynamically aggregate the base contract view model price with the add-on package cost
                            viewModel.setPrice(viewModel.getPrice().add(additionalPackage.getPrice()));
                        }
                    }
                    return viewModel;
                })
                .toList();
    }

    /**
     * Assembles core profile statistics and financial liabilities for a user summary window.
     * Enriches profiles using external system metrics and incorporates graceful fallbacks.
     */
    @Override
    public UserViewModel getUserInfo(String username) {

        UserEntity userFromRepo = userRepository.findByUsername(username).orElse(null);
        UserViewModel userViewModel = new UserViewModel();
        if (userFromRepo != null){
            userViewModel
                    .setUsername(userFromRepo.getUsername())
                    .setFirstName(userFromRepo.getFirstName())
                    .setLastName(userFromRepo.getLastName())
                    .setEmail(userFromRepo.getEmail())
                    .setTotalDebtPerMonth(userFromRepo.getTotalDebtPerMonth());

//          Enrich with loyalty data from the microservice (degrades gracefully if it is down).
            LoyaltyResponse loyalty = loyaltyFacade.getBalance(username);
            if (loyalty != null) {
                userViewModel
                        .setLoyaltyPoints(loyalty.getPointsBalance())
                        .setLoyaltyTier(loyalty.getTier());
            } else {
                userViewModel.setLoyaltyTier("—");
            }
        }

        return userViewModel;
    }

    /**
     * Executes loyalty point redemptions against an external service, processing the resulting
     * monetary reward as a statement credit toward the client's monthly balance.
     *
     * @param username The identifier of the profile redeeming points.
     * @param points   The aggregate amount of rewards points to process.
     * @return         The exact currency discount value successfully applied to the profile ledger.
     * @throws LoyaltyException If the client cannot be found, has insufficient funds, or the upstream API fails.
     */
    @Override
    @Transactional
    public BigDecimal redeemLoyaltyPoints(String username, int points) {

        UserEntity user = findUserByUsername(username);
        if (user == null) {
            throw new bg.greencom.greencomwebapp.client.LoyaltyException("User not found.");
        }

//      Deduct points in the loyalty-service; throws LoyaltyException on insufficient balance / outage.
        LoyaltyResponse loyalty = loyaltyFacade.redeem(username, points);

//      Apply the returned BGN discount to the user's monthly debt (floored at zero).
        BigDecimal discount = loyalty.getDiscountBgn() == null ? BigDecimal.ZERO : loyalty.getDiscountBgn();
        BigDecimal newDebt = user.getTotalDebtPerMonth().subtract(discount).max(BigDecimal.ZERO);
        user.setTotalDebtPerMonth(newDebt);
        userRepository.saveAndFlush(user);

        return discount;
    }

    /**
     * Evaluates a contract lifespan to see if termination triggers early cancellation liabilities.
     *
     * @param id The unique identifier of the target subscription agreement.
     * @return   True if early-exit compliance penalty terms are enforceable; false if naturally expired.
     */
    @Override
    @Transactional
    public boolean isPenaltyRequired(Long id) {

        ContractViewModel userContract = contractService.findById(id);
        PlanViewModel userPlan = planService.findPlanById(userContract.getPlanId());

//        Check if contract has expired
        long monthsBetween = ChronoUnit.MONTHS.between(userContract.getSignedOn(), LocalDate.now());
//        Contract has not expired
        return monthsBetween <= Integer.parseInt(userPlan.getPlanDuration());
    }

    /**
     * Calculates early termination financial penalties based on standard provider criteria.
     * Liquidated damages are capped at a maximum of 3 months of service fees.
     *
     * @param id The unique identifier of the terminating subscription contract.
     * @return   The definitive liquidated penalty amount owed by the client.
     */
    @Override
    @Transactional
    public BigDecimal calculatePenalty(Long id) {

        BigDecimal penaltyAmount = BigDecimal.ZERO;
        BigDecimal penaltyMonths = BigDecimal.ZERO;

        ContractViewModel userContract = contractService.findById(id);
        PlanViewModel userPlan = planService.findPlanById(userContract.getPlanId());

        long monthsBetween = ChronoUnit.MONTHS.between(userContract.getSignedOn(), LocalDate.now());

        // Standard Penalty Cap Rule: If more than 3 months remain, liability caps out at 3 months of fees.
        // Otherwise, the client only owes a payout matching the exact remaining fraction of their term.
        if (monthsBetween <= Long.parseLong(userPlan.getPlanDuration()) - 3){
            penaltyMonths = BigDecimal.valueOf(3);
        } else {
            penaltyMonths = BigDecimal.valueOf(Long.parseLong(userPlan.getPlanDuration()) - monthsBetween);
        }

        // Aggregate full liability amount by multiplying target months against base service rate.
        penaltyAmount = userPlan.getPrice().multiply(penaltyMonths);

        return penaltyAmount;
    }

}