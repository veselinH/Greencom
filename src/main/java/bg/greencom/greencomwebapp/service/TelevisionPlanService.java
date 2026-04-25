package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.TelevisionPlanEntity;
import bg.greencom.greencomwebapp.model.service.TelevisionPlanServiceModel;
import bg.greencom.greencomwebapp.model.view.TelevisionPlanViewModel;

import java.util.Set;

public interface TelevisionPlanService {
    Set<TelevisionPlanViewModel> findAllPlansOrderedByPrice();

    void addPlan(TelevisionPlanServiceModel televisionPlanServiceModel);

    TelevisionPlanViewModel findById(Long id);

    TelevisionPlanEntity findEntityById(Long planId);
}
