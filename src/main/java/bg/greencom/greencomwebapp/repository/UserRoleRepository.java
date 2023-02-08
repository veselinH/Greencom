package bg.greencom.greencomwebapp.repository;

import bg.greencom.greencomwebapp.model.entity.UserRoleEntity;
import bg.greencom.greencomwebapp.model.entity.enums.UserRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
    Optional<UserRoleEntity> findByName(UserRoleEnum name);
}
