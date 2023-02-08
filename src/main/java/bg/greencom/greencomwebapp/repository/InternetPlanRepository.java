package bg.greencom.greencomwebapp.repository;

import bg.greencom.greencomwebapp.model.entity.InternetPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InternetPlanRepository extends JpaRepository<InternetPlanEntity, Long> {
}
