package bg.greencom.greencomwebapp.repository;

import bg.greencom.greencomwebapp.model.entity.AdditionalPackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdditionalPackageRepository extends JpaRepository<AdditionalPackageEntity, Long> {
    List<AdditionalPackageEntity> findAllByOrderByNameAsc();
}
