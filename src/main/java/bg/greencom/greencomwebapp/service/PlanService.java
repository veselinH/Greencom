package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.PlanEntity;
import bg.greencom.greencomwebapp.model.view.PlanViewModel;

/**
 * General-purpose plan query facade that operates across all plan sub-types
 * through the shared {@code PlanEntity} inheritance hierarchy.
 */
public interface PlanService {

    /**
     * Returns an active plan by its unique name.
     * Used primarily for duplicate-name validation when creating new plans.
     */
    PlanEntity findActivePlanByName(String name);

    /** Returns a generic plan view model by ID. Throws {@code PlanNotFoundException} if not found. */
    PlanViewModel findPlanById(Long planId);
}
