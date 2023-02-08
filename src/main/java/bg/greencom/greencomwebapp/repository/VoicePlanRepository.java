package bg.greencom.greencomwebapp.repository;

import bg.greencom.greencomwebapp.model.entity.VoicePlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoicePlanRepository extends JpaRepository<VoicePlanEntity, Long> {
}
