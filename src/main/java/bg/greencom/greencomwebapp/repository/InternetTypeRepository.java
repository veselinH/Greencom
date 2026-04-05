package bg.greencom.greencomwebapp.repository;

import bg.greencom.greencomwebapp.model.entity.InternetTypeEntity;
import bg.greencom.greencomwebapp.model.entity.enums.InternetTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InternetTypeRepository extends JpaRepository<InternetTypeEntity, Long> {
    InternetTypeEntity findByName(InternetTypeEnum name);
}
