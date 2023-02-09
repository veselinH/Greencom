package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.model.entity.UserRoleEntity;
import bg.greencom.greencomwebapp.model.entity.enums.UserRoleEnum;
import bg.greencom.greencomwebapp.model.service.UserServiceModel;
import bg.greencom.greencomwebapp.repository.UserRepository;
import bg.greencom.greencomwebapp.service.UserRoleService;
import bg.greencom.greencomwebapp.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRoleService userRoleService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRoleService userRoleService, UserRepository userRepository, ModelMapper modelMapper) {
        this.userRoleService = userRoleService;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
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
                    .setPassword("12345")
                    .setRegisteredOn(LocalDateTime.now());

            userRepository.save(user);
        }
    }

    @Override
    public UserServiceModel registerUser(UserServiceModel userServiceModel) {
        UserEntity user = modelMapper.map(userServiceModel, UserEntity.class);
        UserRoleEntity userRole = userRoleService.findByName(UserRoleEnum.USER);

        user.setRegisteredOn(LocalDateTime.now());
        user.getRoles().add(userRole);
        user.setTotalDebtPerMonth(BigDecimal.ZERO);

        userRepository.save(user);

        return userServiceModel;
    }
}
