package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.*;
import bg.greencom.greencomwebapp.model.entity.enums.UserRoleEnum;
import bg.greencom.greencomwebapp.model.service.UserServiceModel;
import bg.greencom.greencomwebapp.model.user.GreencomUserDetails;
import bg.greencom.greencomwebapp.model.view.DataPlanViewModel;
import bg.greencom.greencomwebapp.model.view.VoicePlanViewModel;
import bg.greencom.greencomwebapp.repository.UserRepository;
import bg.greencom.greencomwebapp.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
    private final SignatureService signatureService;

    public UserServiceImpl(UserRoleService userRoleService, UserRepository userRepository, ModelMapper modelMapper, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, VoicePlanService voicePlanService, DataPlanService dataPlanService, SignatureService signatureService) {
        this.userRoleService = userRoleService;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.voicePlanService = voicePlanService;
        this.dataPlanService = dataPlanService;
        this.signatureService = signatureService;
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
    public void addVoicePlan(VoicePlanViewModel voicePlan, GreencomUserDetails userDetails, byte[] imageBytes) {
//    Add voice plan to the user and increase the debt
//      Retrieve both user and voicePlan from the database
        UserEntity user = this.findUserByUsername(userDetails.getUsername());
        VoicePlanEntity voicePlanFromDB = voicePlanService.findByName(voicePlan.getName());
//      Add the new plan to the user entity
        user.getUserVoiceMobilePlans().add(voicePlanFromDB);
//      Increase the total debt
        BigDecimal totalDebt = user.getTotalDebtPerMonth();
        totalDebt = totalDebt.add(voicePlanFromDB.getPrice());
        user.setTotalDebtPerMonth(totalDebt);
//      Save to database by updating the user
        userRepository.saveAndFlush(user);
//      Save the signature
        signatureService.addSignature(voicePlanFromDB, user, imageBytes);

    }

    @Override
    public void removePlan(String name, GreencomUserDetails userDetails, PlanEntity planToDelete) {
        UserEntity user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        user.getUserSignatures().removeIf(signature -> Objects.equals(signature.getUser().getId(), user.getId()) && Objects.equals(signature.getPlan().getId(), planToDelete.getId()));
        user.setTotalDebtPerMonth(user.getTotalDebtPerMonth().subtract(planToDelete.getPrice()));
        user.getUserAllPlans().remove(planToDelete);
        userRepository.saveAndFlush(user);
    }

    @Override
    public void addDataPlan(DataPlanViewModel dataPlan, GreencomUserDetails userDetails) {
//      Add voice plan to the user and increase the debt
//      Retrieve both user and voicePlan from the database
        UserEntity user = this.findUserByUsername(userDetails.getUsername());
        DataPlanEntity dataPlanFromDB = dataPlanService.findByName(dataPlan.getName());
//      Add the new plan to the user entity
        user.getUserDataPlans().add(dataPlanFromDB);
//      Increase the total debt
        BigDecimal totalDebt = user.getTotalDebtPerMonth();
        totalDebt = totalDebt.add(dataPlanFromDB.getPrice());
        user.setTotalDebtPerMonth(totalDebt);
//      Save to database by updating the user
        userRepository.saveAndFlush(user);
    }

    @Override
    public List<VoicePlanViewModel> getAllVoicePlans(String username) {
        return userRepository.findByUsername(username)
                .map(user -> user.getUserVoiceMobilePlans().stream()
                        .map(voicePlan -> modelMapper.map(voicePlan, VoicePlanViewModel.class))
                        .toList())
                .orElse(Collections.emptyList());
    }
}
