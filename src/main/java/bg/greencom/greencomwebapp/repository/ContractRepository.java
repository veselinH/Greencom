package bg.greencom.greencomwebapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<bg.greencom.greencomwebapp.model.entity.ContractEntity, Long> {
    Long id(Long id);
}
