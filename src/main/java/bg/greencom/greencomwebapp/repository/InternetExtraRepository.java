package bg.greencom.greencomwebapp.repository;

import bg.greencom.greencomwebapp.model.entity.InternetExtrasEntity;
import bg.greencom.greencomwebapp.model.entity.enums.InternetExtraEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InternetExtraRepository extends JpaRepository<InternetExtrasEntity, Long> {
    Optional<InternetExtrasEntity> findByName(InternetExtraEnum internetExtraEnum);
}
