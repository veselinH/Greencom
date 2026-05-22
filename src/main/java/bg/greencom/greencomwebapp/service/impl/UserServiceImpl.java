package bg.greencom.greencomwebapp.service.impl;

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
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

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

    public UserServiceImpl(UserRoleService userRoleService, UserRepository userRepository, ModelMapper modelMapper, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, VoicePlanService voicePlanService, DataPlanService dataPlanService, PlanService planService, ContractService contractService, InternetPlanService internetPlanService, TelevisionPlanService televisionPlanService, AdditionalPackageService additionalPackageService) {
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
    }

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

    @Override
    public void signVoicePlan(VoicePlanViewModel voicePlan, GreencomUserDetails userDetails, byte[] signSignature) {
//    Add voice plan to the user and increase the debt
//      Retrieve both user and voicePlan from the database
        UserEntity user = this.findUserByUsername(userDetails.getUsername());
        VoicePlanEntity voicePlanFromDB = voicePlanService.findEntityById(voicePlan.getId());
//      Add the new plan to the user entity
        user.getUserVoiceMobilePlans().add(voicePlanFromDB);
//      Increase the total debt
        BigDecimal totalDebt = user.getTotalDebtPerMonth();
        totalDebt = totalDebt.add(voicePlanFromDB.getPrice());
        user.setTotalDebtPerMonth(totalDebt);
//      Save to database by updating the user
        userRepository.saveAndFlush(user);
//      Save the contract
        contractService.addContract(voicePlanFromDB, user,null,  signSignature);

    }

    @Override
    public void signDataPlan(DataPlanViewModel dataPlan, GreencomUserDetails userDetails, byte[] signSignature) {
//      Add data plan to the user and increase the debt
//      Retrieve both user and dataPlan from the database
        UserEntity user = this.findUserByUsername(userDetails.getUsername());
        DataPlanEntity dataPlanFromDB = dataPlanService.findEntityById(dataPlan.getId());
//      Add the new plan to the user entity
        user.getUserDataPlans().add(dataPlanFromDB);
//      Increase the total debt
        BigDecimal totalDebt = user.getTotalDebtPerMonth();
        totalDebt = totalDebt.add(dataPlanFromDB.getPrice());
        user.setTotalDebtPerMonth(totalDebt);
//      Save to database by updating the user
        userRepository.saveAndFlush(user);
//      Save the contract
        contractService.addContract(dataPlanFromDB, user, null, signSignature);
    }

    @Override
    @Transactional
    public String unsignPlan(Long contractId, String username, byte[] unsignSignature) {

        final StringBuilder planName = new StringBuilder();

        UserEntity user = findUserByUsername(username);
        ContractViewModel userContract = contractService.findById(contractId);
        PlanViewModel userPlan = planService.findPlanById(userContract.getPlanId());
        planName.append(userPlan.getName());
//        Lower the dept of the user
        BigDecimal totalDebt = user.getTotalDebtPerMonth();
        totalDebt = totalDebt.subtract(userPlan.getPrice());
        if (userContract.getAdditionalPackageViewModels() != null) {
            for (AdditionalPackageViewModel additionalPackage : userContract.getAdditionalPackageViewModels()){
                totalDebt = totalDebt.subtract(additionalPackage.getPrice());
            }
        }

        user.setTotalDebtPerMonth(totalDebt);

        userRepository.saveAndFlush(user);

        contractService.deactivateContract(contractId, unsignSignature);

        return planName.toString();

    }

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

    @Override
    public void signInternetPlan(InternetPlanViewModel internetPlan, GreencomUserDetails userDetails, byte[] signSignature) {
//      Add internet plan to the user and increase the debt
//      Retrieve both user and internetPlan from the database
        UserEntity user = this.findUserByUsername(userDetails.getUsername());
        InternetPlanEntity internetPlanFromDB = internetPlanService.findEntityById(internetPlan.getId());
//      Add the new plan to the user entity
        user.getUserInternetPlans().add(internetPlanFromDB);
//      Increase the total debt
        BigDecimal totalDebt = user.getTotalDebtPerMonth();
        totalDebt = totalDebt.add(internetPlanFromDB.getPrice());
        user.setTotalDebtPerMonth(totalDebt);
//      Save to database by updating the user
        userRepository.saveAndFlush(user);
//      Save the contract
        contractService.addContract(internetPlanFromDB, user, null, signSignature);
    }

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

    @Override
    public void signTelevisionPlan(Long planId, Set<Long> additionalPackageIds, GreencomUserDetails userDetails, byte[] signSignature) {
//      Add television plan to the user and increase the debt
//      Retrieve both user and televisionPlan from the database
        UserEntity user = this.findUserByUsername(userDetails.getUsername());
        TelevisionPlanEntity televisionPlanFromDB = televisionPlanService.findEntityById(planId);
        Set<AdditionalPackageEntity> additionalPackagesFromDB = additionalPackageService.findAllByIds(additionalPackageIds);
//      Add the new plan to the user entity
        user.getUserTelevisionPlans().add(televisionPlanFromDB);
//      Increase the total debt
        BigDecimal totalDebt = user.getTotalDebtPerMonth();
        totalDebt = totalDebt.add(televisionPlanFromDB.getPrice());
        for (AdditionalPackageEntity packageEntity : additionalPackagesFromDB) {
            totalDebt = totalDebt.add(packageEntity.getPrice());
        }
        user.setTotalDebtPerMonth(totalDebt);
//      Save to database by updating the user
        userRepository.saveAndFlush(user);
//      Save the contract
        contractService.addContract(televisionPlanFromDB, user, additionalPackagesFromDB, signSignature);
    }

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
                            viewModel.setPrice(viewModel.getPrice().add(additionalPackage.getPrice()));
                        }
                    }
                    return viewModel;
                })
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
        }

        return userViewModel;
    }

}