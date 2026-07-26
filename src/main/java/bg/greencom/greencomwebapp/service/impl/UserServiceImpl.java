package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.client.LoyaltyFacade;
import bg.greencom.greencomwebapp.client.dto.LoyaltyResponse;
import bg.greencom.greencomwebapp.model.binding.UserProfileEditBindingModel;
import bg.greencom.greencomwebapp.model.entity.*;
import bg.greencom.greencomwebapp.model.entity.enums.UserRoleEnum;
import bg.greencom.greencomwebapp.model.service.UserServiceModel;
import bg.greencom.greencomwebapp.model.user.GreencomUserDetails;
import bg.greencom.greencomwebapp.model.view.*;
import bg.greencom.greencomwebapp.repository.UserRepository;
import bg.greencom.greencomwebapp.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

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

    @Value("${greencom.admin.username}")
    private String adminUsername;

    @Value("${greencom.admin.password}")
    private String adminPassword;

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

    @Override
    public void initialize() {
        if (userRepository.count() == 0) {
            UserEntity user = new UserEntity();
            user.getRoles().add(userRoleService.findByName(UserRoleEnum.ADMIN));
            user.getRoles().add(userRoleService.findByName(UserRoleEnum.USER));
            user.getRoles().add(userRoleService.findByName(UserRoleEnum.MODERATOR));

            user
                    .setUsername(adminUsername)
                    .setFirstName("Veselin")
                    .setLastName("Hristov")
                    .setEmail("admin@greencom.bg")
                    .setTotalDebtPerMonth(BigDecimal.ZERO)
                    .setPassword(passwordEncoder.encode(adminPassword))
                    .setRegisteredOn(LocalDateTime.now());

            userRepository.save(user);
            LOGGER.info("Admin user created successfully.");
        }
    }

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
        LOGGER.info("User {} registered successfully.", userServiceModel.getUsername());

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

    @Override
    public void signVoicePlan(VoicePlanViewModel voicePlan, GreencomUserDetails userDetails, byte[] signSignature) {

        UserEntity user = this.findUserByUsername(userDetails.getUsername());
        VoicePlanEntity voicePlanFromDB = voicePlanService.findEntityById(voicePlan.getId());

        user.getUserVoiceMobilePlans().add(voicePlanFromDB);

        BigDecimal totalDebt = user.getTotalDebtPerMonth();
        totalDebt = totalDebt.add(voicePlanFromDB.getPrice());
        user.setTotalDebtPerMonth(totalDebt);

        userRepository.saveAndFlush(user);
        contractService.addContract(voicePlanFromDB, user,null,  signSignature);
        LOGGER.info("User {} signed voice plan {} successfully.", user.getUsername(), voicePlanFromDB.getName());
    }

    @Override
    public void signDataPlan(DataPlanViewModel dataPlan, GreencomUserDetails userDetails, byte[] signSignature) {

        UserEntity user = this.findUserByUsername(userDetails.getUsername());
        DataPlanEntity dataPlanFromDB = dataPlanService.findEntityById(dataPlan.getId());

        user.getUserDataPlans().add(dataPlanFromDB);

        BigDecimal totalDebt = user.getTotalDebtPerMonth();
        totalDebt = totalDebt.add(dataPlanFromDB.getPrice());
        user.setTotalDebtPerMonth(totalDebt);

        userRepository.saveAndFlush(user);
        contractService.addContract(dataPlanFromDB, user, null, signSignature);
        LOGGER.info("User {} signed data plan {} successfully.", user.getUsername(), dataPlanFromDB.getName());
    }

    @Override
    @Transactional
    public String unsignPlan(Long contractId, String username, byte[] unsignSignature) {

        StringBuilder planName = new StringBuilder();

        UserEntity user = findUserByUsername(username);
        ContractViewModel userContract = contractService.findById(contractId);
        PlanViewModel userPlan = planService.findPlanById(userContract.getPlanId());

        BigDecimal totalDebt = user.getTotalDebtPerMonth();
        totalDebt = totalDebt.subtract(userPlan.getPrice());

        if (userContract.getAdditionalPackageViewModels() != null) {
            for (AdditionalPackageViewModel additionalPackage : userContract.getAdditionalPackageViewModels()){
                totalDebt = totalDebt.subtract(additionalPackage.getPrice());
            }
        }

        user.setTotalDebtPerMonth(totalDebt);

        if (user.getTotalDebtPerMonth().compareTo(BigDecimal.ZERO) < 0) {
            user.setTotalDebtPerMonth(BigDecimal.ZERO);
        }

        userRepository.saveAndFlush(user);
        contractService.deactivateContract(contractId, unsignSignature);
        LOGGER.info("User {} unsigned contract {} successfully.", user.getUsername(), contractId);

        planName.append(userPlan.getName());
        return planName.toString();

    }

    @Override
    @Transactional(readOnly = true)
    public List<VoicePlanViewModel> getAllVoicePlans(String username) {

        UserEntity user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return Collections.emptyList();

        return user.getUserContracts().stream()
                .filter(ContractEntity::isActive)
                .filter(contract -> contract.getPlan() instanceof VoicePlanEntity)
                .map(this::mapToVoicePlanViewModel)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataPlanViewModel> getAllDataPlans(String username) {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return Collections.emptyList();

        return user.getUserContracts().stream()
                .filter(ContractEntity::isActive)
                .filter(contract -> contract.getPlan() instanceof DataPlanEntity)
                .map(this::mapToDataPlanViewModel)
                .toList();
    }

    @Override
    public void signInternetPlan(InternetPlanViewModel internetPlan, GreencomUserDetails userDetails, byte[] signSignature) {

        UserEntity user = this.findUserByUsername(userDetails.getUsername());
        InternetPlanEntity internetPlanFromDB = internetPlanService.findEntityById(internetPlan.getId());

        user.getUserInternetPlans().add(internetPlanFromDB);

        BigDecimal totalDebt = user.getTotalDebtPerMonth();
        totalDebt = totalDebt.add(internetPlanFromDB.getPrice());
        user.setTotalDebtPerMonth(totalDebt);

        userRepository.saveAndFlush(user);
        contractService.addContract(internetPlanFromDB, user, null, signSignature);
        LOGGER.info("User {} signed internet plan {} successfully.", user.getUsername(), internetPlanFromDB.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InternetPlanViewModel> getAllInternetPlans(String username) {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return Collections.emptyList();

        return user.getUserContracts().stream()
                .filter(ContractEntity::isActive)
                .filter(contract -> contract.getPlan() instanceof InternetPlanEntity)
                .map(this::mapToInternetPlanViewModel)
                .toList();
    }

    @Override
    public void signTelevisionPlan(Long planId, Set<Long> additionalPackageIds, GreencomUserDetails userDetails, byte[] signSignature) {

        UserEntity user = this.findUserByUsername(userDetails.getUsername());
        TelevisionPlanEntity televisionPlanFromDB = televisionPlanService.findEntityById(planId);
        Set<AdditionalPackageEntity> additionalPackagesFromDB = additionalPackageService.findAllByIds(additionalPackageIds);

        user.getUserTelevisionPlans().add(televisionPlanFromDB);

        BigDecimal totalDebt = user.getTotalDebtPerMonth();
        totalDebt = totalDebt.add(televisionPlanFromDB.getPrice());
        for (AdditionalPackageEntity packageEntity : additionalPackagesFromDB) {
            totalDebt = totalDebt.add(packageEntity.getPrice());
        }
        user.setTotalDebtPerMonth(totalDebt);

        userRepository.saveAndFlush(user);
        contractService.addContract(televisionPlanFromDB, user, additionalPackagesFromDB, signSignature);
        LOGGER.info("User {} signed television plan {} successfully.", user.getUsername(), televisionPlanFromDB.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TelevisionPlanViewModel> getAllTelevisionPlans(String username) {
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return Collections.emptyList();

        return user.getUserContracts().stream()
                .filter(ContractEntity::isActive)
                .filter(contract -> contract.getPlan() instanceof TelevisionPlanEntity)
                .map(this::mapToTelevisionPlanViewModel)
                .toList();
    }

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

    @Override
    @Transactional
    public BigDecimal redeemLoyaltyPoints(String username, int points) {

        UserEntity user = findUserByUsername(username);
        if (user == null) {
            throw new bg.greencom.greencomwebapp.client.LoyaltyException("User not found.");
        }

        LoyaltyResponse loyalty = loyaltyFacade.redeem(username, points);

        BigDecimal discount = loyalty.getDiscountEur() == null ? BigDecimal.ZERO : loyalty.getDiscountEur();
        BigDecimal newDebt = user.getTotalDebtPerMonth().subtract(discount).max(BigDecimal.ZERO);
        user.setTotalDebtPerMonth(newDebt);
        userRepository.saveAndFlush(user);
        LOGGER.info("User {} redeemed {} points from loyalty service.", username, points);

        return discount;
    }

    @Override
    @Transactional
    public boolean isPenaltyRequired(Long id) {

        ContractViewModel userContract = contractService.findById(id);
        PlanViewModel userPlan = planService.findPlanById(userContract.getPlanId());

        long monthsBetween = ChronoUnit.MONTHS.between(userContract.getSignedOn(), LocalDate.now());
        return monthsBetween <= Integer.parseInt(userPlan.getPlanDuration());
    }

    @Override
    @Transactional
    public BigDecimal calculatePenalty(Long id) {

        BigDecimal penaltyAmount;
        BigDecimal penaltyMonths;

        ContractViewModel userContract = contractService.findById(id);
        PlanViewModel userPlan = planService.findPlanById(userContract.getPlanId());

        long monthsBetween = ChronoUnit.MONTHS.between(userContract.getSignedOn(), LocalDate.now());

        if (monthsBetween <= Long.parseLong(userPlan.getPlanDuration()) - 3){
            penaltyMonths = BigDecimal.valueOf(3);
        } else {
            penaltyMonths = BigDecimal.valueOf(Long.parseLong(userPlan.getPlanDuration()) - monthsBetween);
        }

        penaltyAmount = userPlan.getPrice().multiply(penaltyMonths);

        return penaltyAmount;
    }

    @Override
    public List<UserViewModel> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserViewModel)
                .toList();
    }

    @Override
    @Transactional
    public boolean addRole(String username, String role) {
        UserRoleEntity roleEntity = userRoleService.findByName(UserRoleEnum.valueOf(role));
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            user.getRoles().add(roleEntity);
            userRepository.saveAndFlush(user);
            LOGGER.info("Role {} added to user {}.", role, username);
        } else {
            LOGGER.error("Failed to add role {} to user {}: User not found.", role, username);
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public boolean removeRole(String username, String role) {
        UserRoleEntity roleEntity = userRoleService.findByName(UserRoleEnum.valueOf(role));
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            user.getRoles().remove(roleEntity);
            userRepository.saveAndFlush(user);
            LOGGER.info("Role {} removed from user {}.", role, username);
        } else {
            LOGGER.error("Failed to remove role {} from user {}: User not found.", role, username);
            return false;
        }
        return true;
    }

    @Override
    public boolean editUserProfile(String username, UserProfileEditBindingModel userProfileEditBindingModel) {

        UserEntity user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            user.setFirstName(userProfileEditBindingModel.getFirstName());
            user.setLastName(userProfileEditBindingModel.getLastName());
            user.setEmail(userProfileEditBindingModel.getEmail());
            userRepository.saveAndFlush(user);
            LOGGER.info("User profile for '{}' edited successfully.", username);
        } else {
            LOGGER.error("Failed to edit user profile for '{}': User not found.", username);
            return false;
        }

        return true;
    }

    private UserViewModel mapToUserViewModel(UserEntity userEntity) {
        UserViewModel viewModel = modelMapper.map(userEntity, UserViewModel.class);
        viewModel.setRoles(userEntity.getRoles().stream().map(userRoleEntity -> userRoleEntity.getName().getValue()).collect(Collectors.toSet()));
        return viewModel;
    }

    private VoicePlanViewModel mapToVoicePlanViewModel(ContractEntity contract) {
        VoicePlanViewModel viewModel = modelMapper.map(contract.getPlan(), VoicePlanViewModel.class);
        viewModel.setContractId(contract.getId());
        viewModel.setMobileNumber(contract.getMobileNumber());
        return viewModel;
    }

    private DataPlanViewModel mapToDataPlanViewModel(ContractEntity contract) {
        DataPlanViewModel viewModel = modelMapper.map(contract.getPlan(), DataPlanViewModel.class);
        viewModel.setContractId(contract.getId());
        return viewModel;
    }

    private InternetPlanViewModel mapToInternetPlanViewModel(ContractEntity contract) {
        InternetPlanViewModel viewModel = modelMapper.map(contract.getPlan(), InternetPlanViewModel.class);
        viewModel.setInternetType(((InternetPlanEntity) contract.getPlan()).getInternetType().getName().getValue());
        viewModel.setContractId(contract.getId());
        return viewModel;
    }

    private TelevisionPlanViewModel mapToTelevisionPlanViewModel(ContractEntity contract) {
        TelevisionPlanViewModel viewModel = modelMapper.map(contract.getPlan(), TelevisionPlanViewModel.class);
        viewModel.setTelevisionType(((TelevisionPlanEntity) contract.getPlan()).getTelevisionType().getName().getValue());
        viewModel.setContractId(contract.getId());
        if (contract.getAdditionalPackageEntities() != null) {
            for (AdditionalPackageEntity additionalPackage : contract.getAdditionalPackageEntities()) {
                AdditionalPackageViewModel additionalPackageViewModel = modelMapper.map(additionalPackage, AdditionalPackageViewModel.class);
                additionalPackageViewModel.setName(additionalPackage.getName().getValue());
                viewModel.getAdditionalPackages().add(additionalPackageViewModel);

                viewModel.setPrice(viewModel.getPrice().add(additionalPackage.getPrice()));
            }
        }
        return viewModel;
    }
}