package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.model.entity.UserRoleEntity;
import bg.greencom.greencomwebapp.model.entity.enums.UserRoleEnum;
import bg.greencom.greencomwebapp.repository.UserRoleRepository;
import bg.greencom.greencomwebapp.service.UserRoleService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public UserRoleServiceImpl(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public void initialize() {
     if (userRoleRepository.count() == 0){
         UserRoleEntity roleUser = new UserRoleEntity();
         roleUser.setName(UserRoleEnum.USER);
         UserRoleEntity roleAdmin = new UserRoleEntity();
         roleAdmin.setName(UserRoleEnum.ADMIN);

         userRoleRepository.saveAll(Set.of(roleUser, roleAdmin));
     }
    }

    @Override
    public UserRoleEntity findByName(UserRoleEnum userRoleEnum) {
        return userRoleRepository
                .findByName(userRoleEnum)
                .orElse(null);
        // TODO: orElse must be changed error handling
    }
}
