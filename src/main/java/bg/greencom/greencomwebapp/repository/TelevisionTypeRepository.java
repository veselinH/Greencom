package bg.greencom.greencomwebapp.repository;

import bg.greencom.greencomwebapp.model.entity.TelevisionTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelevisionTypeRepository extends JpaRepository<TelevisionTypeEntity, Long> {
}
