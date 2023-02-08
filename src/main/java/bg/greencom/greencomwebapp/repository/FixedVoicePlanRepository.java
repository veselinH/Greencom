package bg.greencom.greencomwebapp.repository;

import bg.greencom.greencomwebapp.model.entity.FixedVoicePlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FixedVoicePlanRepository extends JpaRepository<FixedVoicePlanEntity, Long> {
}
