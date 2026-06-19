package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.TelevisionPlanEntity;
import bg.greencom.greencomwebapp.model.service.TelevisionPlanServiceModel;
import bg.greencom.greencomwebapp.model.view.TelevisionPlanViewModel;

import java.util.Set;

/**
 * Manages television subscription plans.
 */
public interface TelevisionPlanService {

    /** Returns all television plans sorted by price ascending. */
    Set<TelevisionPlanViewModel> findAllPlansOrderedByPrice();

    /** Persists a new television plan. */
    void addPlan(TelevisionPlanServiceModel televisionPlanServiceModel);

    /** Returns the view model for a television plan. Throws {@code PlanNotFoundException} if not found. */
    TelevisionPlanViewModel findById(Long id);

    /** Returns the raw JPA entity for internal service use. Throws {@code PlanNotFoundException} if not found. */
    TelevisionPlanEntity findEntityById(Long planId);

    /** Updates the fields of an existing television plan. */
    void updateTelevisionPlan(TelevisionPlanServiceModel televisionPlanServiceModel);
}
