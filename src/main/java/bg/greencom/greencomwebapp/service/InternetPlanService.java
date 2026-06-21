package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.InternetPlanEntity;
import bg.greencom.greencomwebapp.model.service.InternetPlanServiceModel;
import bg.greencom.greencomwebapp.model.view.InternetPlanViewModel;

import java.util.List;

/**
 * Manages fixed-line internet plans.
 */
public interface InternetPlanService {

    /** Persists a new internet plan. */
    void addPlan(InternetPlanServiceModel internetPlanServiceModel);

    /** Returns all internet plans sorted by price ascending. */
    List<InternetPlanViewModel> findAllPlansOrderedByPrice();

    /** Returns the view model for an internet plan. Throws {@code PlanNotFoundException} if not found. */
    InternetPlanViewModel findById(Long id);

    /** Updates the fields of an existing internet plan. */
    void updateInternetPlan(InternetPlanServiceModel internetPlanServiceModel);

    /** Returns the raw JPA entity for internal service use. Throws {@code PlanNotFoundException} if not found. */
    InternetPlanEntity findEntityById(Long id);
}
