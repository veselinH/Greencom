package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.UserRoleEntity;
import bg.greencom.greencomwebapp.model.entity.enums.UserRoleEnum;
import bg.greencom.greencomwebapp.repository.UserRoleRepository;
import bg.greencom.greencomwebapp.service.UserRoleService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Service implementation responsible for provisioning, initialization,
 * and retrieval of system access roles and application security privileges.
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    private static final String OBJECT_TYPE = "user role";
    private final static Logger LOGGER = LoggerFactory.getLogger(UserRoleServiceImpl.class);

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
         LOGGER.info("User roles initialized.");
     }
    }

    @Override
    public UserRoleEntity findByName(UserRoleEnum userRoleEnum) {
        return userRoleRepository
                .findByName(userRoleEnum)
                .orElseThrow(() -> new ObjectNotFoundException(
                        userRoleEnum,
                        OBJECT_TYPE
                ));
    }
}
