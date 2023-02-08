package bg.greencom.greencomwebapp.repository;

import bg.greencom.greencomwebapp.model.entity.MobileExtraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MobileExtraRepository extends JpaRepository<MobileExtraEntity, Long> {
}
