package bg.greencom.greencomwebapp.repository;

import bg.greencom.greencomwebapp.model.entity.InternetExtrasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InternetExtraRepository extends JpaRepository<InternetExtrasEntity, Long> {
}
