package bg.greencom.greencomwebapp.repository;

import bg.greencom.greencomwebapp.model.entity.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<PlanEntity, Long> {
    Optional<PlanEntity> findByName(String name);
}
