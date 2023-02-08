package bg.greencom.greencomwebapp.repository;

import bg.greencom.greencomwebapp.model.entity.TelevisionPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelevisionPlanRepository extends JpaRepository<TelevisionPlanEntity, Long> {
}
