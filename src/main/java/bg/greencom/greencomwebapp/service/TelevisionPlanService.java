package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.view.TelevisionPlanViewModel;

import java.util.Set;

public interface TelevisionPlanService {
    Set<TelevisionPlanViewModel> findAllPlansOrderedByPrice();
}
