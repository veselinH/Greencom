package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.PlanEntity;
import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.model.view.PlanViewModel;

import java.util.List;

public interface PlanService {
    PlanEntity findPlanByName(String name);

    PlanViewModel findPlanById(Long planId);
}
