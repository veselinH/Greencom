package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.PlanEntity;
import bg.greencom.greencomwebapp.model.view.PlanViewModel;

public interface PlanService {
    PlanEntity findActivePlanByName(String name);

    PlanViewModel findPlanById(Long planId);
}
