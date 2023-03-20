package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.model.entity.UserRoleEntity;
import bg.greencom.greencomwebapp.model.entity.VoicePlanEntity;
import bg.greencom.greencomwebapp.model.entity.enums.UserRoleEnum;
import bg.greencom.greencomwebapp.model.service.UserServiceModel;
import bg.greencom.greencomwebapp.model.user.GreencomUserDetails;
import bg.greencom.greencomwebapp.model.view.VoicePlanViewModel;
import bg.greencom.greencomwebapp.repository.UserRepository;
import bg.greencom.greencomwebapp.service.UserRoleService;
import bg.greencom.greencomwebapp.service.UserService;
import bg.greencom.greencomwebapp.service.VoicePlanService;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRoleService userRoleService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final VoicePlanService voicePlanService;

    public UserServiceImpl(UserRoleService userRoleService, UserRepository userRepository, ModelMapper modelMapper, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, VoicePlanService voicePlanService) {
        this.userRoleService = userRoleService;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.voicePlanService = voicePlanService;
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
    public UserServiceModel registerUser(UserServiceModel userServiceModel) {
        UserEntity user = modelMapper.map(userServiceModel, UserEntity.class);
        UserRoleEntity userRole = userRoleService.findByName(UserRoleEnum.USER);

        user.setPassword(passwordEncoder.encode(userServiceModel.getPassword()));
        user.setRegisteredOn(LocalDateTime.now());
        user.getRoles().add(userRole);
        user.setTotalDebtPerMonth(BigDecimal.ZERO);

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        userDetails.getUsername(),
                        userDetails.getPassword(),
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(auth);
        return userServiceModel;
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
    public void addVoicePlan(VoicePlanViewModel voicePlan, GreencomUserDetails userDetails) {
        UserEntity user = this.findUserByUsername(userDetails.getUsername());
        VoicePlanEntity voicePlanFromDB = voicePlanService.findByName(voicePlan.getName());

        user.getUserVoiceMobilePlans().add(voicePlanFromDB);

        userRepository.saveAndFlush(user);
    }
}
