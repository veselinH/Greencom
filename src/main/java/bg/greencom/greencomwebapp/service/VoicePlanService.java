package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.VoicePlanEntity;
import bg.greencom.greencomwebapp.model.service.VoicePlanServiceModel;
import bg.greencom.greencomwebapp.model.view.VoicePlanViewModel;

import java.util.List;

/**
 * Manages mobile voice plans (call and SMS bundles).
 */
public interface VoicePlanService {

    /** Returns all voice plans sorted by price ascending. */
    List<VoicePlanViewModel> findAllPlansOrderedByPrice();

    /** Persists a new voice plan and returns its saved state. */
    VoicePlanServiceModel addPlan(VoicePlanServiceModel voicePlanServiceModel);

    /** Updates the fields of an existing voice plan. */
    void updatePlan(VoicePlanServiceModel voicePlanServiceModel);

    /** Returns the view model for a voice plan. Throws {@code PlanNotFoundException} if not found. */
    VoicePlanViewModel findById(Long id);

    /** Returns the raw JPA entity for internal service use. Throws {@code PlanNotFoundException} if not found. */
    VoicePlanEntity findEntityById(Long id);
}
