package bg.greencom.greencomwebapp.repository;

import bg.greencom.greencomwebapp.model.entity.VoicePlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoicePlanRepository extends JpaRepository<VoicePlanEntity, Long> {

    @Query("SELECT v FROM VoicePlanEntity v ORDER BY v.price")
    List<VoicePlanEntity> findAllVoicePlansOrderedByPrice();

    Optional<VoicePlanEntity> findByName(String name);
}
