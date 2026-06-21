package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.DataPlanEntity;
import bg.greencom.greencomwebapp.model.service.DataPlanServiceModel;
import bg.greencom.greencomwebapp.model.view.DataPlanViewModel;

import java.util.List;

/**
 * Manages mobile data plans (SIM-only data packages).
 */
public interface DataPlanService {

    /** Persists a new data plan. */
    void addPlan(DataPlanServiceModel dataPlanServiceModel);

    /** Returns all data plans sorted by price ascending. */
    List<DataPlanViewModel> findAllPlansOrderedByPrice();

    /** Returns the view model for a data plan. Throws {@code PlanNotFoundException} if not found. */
    DataPlanViewModel findById(Long id);

    /** Updates the fields of an existing data plan. */
    void updatePlan(DataPlanServiceModel dataPlanServiceModel);

    /** Returns the raw JPA entity for internal service use. Throws {@code PlanNotFoundException} if not found. */
    DataPlanEntity findEntityById(Long id);
}
