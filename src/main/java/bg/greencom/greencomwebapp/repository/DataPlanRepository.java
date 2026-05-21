package bg.greencom.greencomwebapp.repository;

import bg.greencom.greencomwebapp.model.entity.DataPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataPlanRepository extends JpaRepository<DataPlanEntity, Long> {
    Optional<DataPlanEntity> findByName(String name);

    @Query("SELECT d FROM DataPlanEntity d ORDER BY d.active DESC, d.price ASC, d.name ASC")
    List<DataPlanEntity> findAllVoicePlansOrderedByPrice();
}
