package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.service.InternetPlanServiceModel;
import bg.greencom.greencomwebapp.model.view.InternetPlanViewModel;

import java.util.List;

public interface InternetPlanService {
    void addPlan(InternetPlanServiceModel internetPlanServiceModel);

    List<InternetPlanViewModel> findAllPlansOrderedByPrice();
}
