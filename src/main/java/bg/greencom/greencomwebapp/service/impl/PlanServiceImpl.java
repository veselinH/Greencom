package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.PlanEntity;
import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.repository.PlanRepository;
import bg.greencom.greencomwebapp.service.PlanService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
@Service
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;

    public PlanServiceImpl(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public PlanEntity findPlanByName(String name) {
        return planRepository.findByName(name).orElse(null);
    }
}



