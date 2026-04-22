package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.PlanEntity;
import bg.greencom.greencomwebapp.model.exception.ObjectNotFoundException;
import bg.greencom.greencomwebapp.model.view.PlanViewModel;
import bg.greencom.greencomwebapp.repository.PlanRepository;
import bg.greencom.greencomwebapp.service.PlanService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class PlanServiceImpl implements PlanService {

    private static final String OBJECT_TYPE = "plan";

    private final PlanRepository planRepository;
    private final ModelMapper modelMapper;

    public PlanServiceImpl(PlanRepository planRepository, ModelMapper modelMapper) {
        this.planRepository = planRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PlanEntity findPlanByName(String name) {
        return planRepository.findByName(name).orElse(null);
    }

    @Override
    public PlanViewModel findPlanById(Long planId) {
        return modelMapper
                .map(planRepository
                        .findById(planId)
                        .orElseThrow(
                                () -> new ObjectNotFoundException(planId, OBJECT_TYPE)),
                        PlanViewModel.class);
    }
}



