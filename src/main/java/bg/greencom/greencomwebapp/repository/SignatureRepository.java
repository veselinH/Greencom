package bg.greencom.greencomwebapp.repository;

import bg.greencom.greencomwebapp.model.entity.SignatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignatureRepository extends JpaRepository<SignatureEntity, Long> {
}
