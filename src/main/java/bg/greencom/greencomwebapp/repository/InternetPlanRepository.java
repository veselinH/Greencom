package bg.greencom.greencomwebapp.repository;

import bg.greencom.greencomwebapp.model.entity.InternetPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface InternetPlanRepository extends JpaRepository<InternetPlanEntity, Long> {
//    @Query("SELECT i FROM InternetPlanEntity i ORDER BY i.price")
//    List<InternetPlanEntity> findAllInternetPlansOrderedByPrice();
      @Query("SELECT i FROM InternetPlanEntity i " +
             "JOIN FETCH i.internetType " +
             "LEFT JOIN FETCH i.internetExtras " +
              "ORDER BY i.price")
      List<InternetPlanEntity> findAllInternetPlansOrderedByPrice();

}
