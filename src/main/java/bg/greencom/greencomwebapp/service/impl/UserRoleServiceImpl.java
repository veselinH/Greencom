package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.UserRoleEntity;
import bg.greencom.greencomwebapp.model.entity.enums.UserRoleEnum;
import bg.greencom.greencomwebapp.repository.UserRoleRepository;
import bg.greencom.greencomwebapp.service.UserRoleService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Service implementation responsible for provisioning, initialization,
 * and retrieval of system access roles and application security privileges.
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    private static final String OBJECT_TYPE = "user role";
    private final UserRoleRepository userRoleRepository;

    public UserRoleServiceImpl(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    /**
     * Seeds the application database with core identity credentials (USER, ADMIN)
     * during the initial bootstrap phase if no prior security records exist.
     */
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

    /**
     * Finds a security role instance by its unique type enumeration.
     *
     * @param userRoleEnum The target application role identifier.
     * @return             The persistent UserRoleEntity configuration.
     * @throws ObjectNotFoundException If the requested security role record does not exist.
     */
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
