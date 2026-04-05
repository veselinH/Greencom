package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.PlanEntity;
import bg.greencom.greencomwebapp.model.entity.UserEntity;

import java.util.List;

public interface PlanService {
    void removePlanAndAdjustDebt(List<UserEntity> users, PlanEntity plan);
}
