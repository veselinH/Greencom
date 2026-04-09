package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.PlanEntity;
import bg.greencom.greencomwebapp.model.entity.UserEntity;

import java.util.List;

public interface PlanService {
    PlanEntity findPlanByName(String name);
}
