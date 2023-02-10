package bg.greencom.greencomwebapp.repository;

import bg.greencom.greencomwebapp.model.entity.MobileExtraEntity;
import bg.greencom.greencomwebapp.model.entity.enums.MobileExtraEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MobileExtraRepository extends JpaRepository<MobileExtraEntity, Long> {
    Optional<MobileExtraEntity> findByName(MobileExtraEnum mobileExtraEnum);
}
