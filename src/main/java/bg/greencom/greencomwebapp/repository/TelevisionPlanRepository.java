package bg.greencom.greencomwebapp.repository;

import bg.greencom.greencomwebapp.model.entity.TelevisionPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TelevisionPlanRepository extends JpaRepository<TelevisionPlanEntity, Long> {

    @Query("SELECT t FROM TelevisionPlanEntity t " +
            "JOIN FETCH t.televisionType " +
            "ORDER BY t.price")
    Set<TelevisionPlanEntity> findAllTelevisionPlansOrderedByPrice();
}
