package bg.greencom.greencomwebapp.repository;

import bg.greencom.greencomwebapp.model.entity.DataPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataPlanRepository extends JpaRepository<DataPlanEntity, Long> {
}
