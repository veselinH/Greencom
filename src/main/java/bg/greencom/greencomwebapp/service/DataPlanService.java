package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.DataPlanEntity;
import bg.greencom.greencomwebapp.model.service.DataPlanServiceModel;
import bg.greencom.greencomwebapp.model.view.DataPlanViewModel;

import java.util.List;

public interface DataPlanService {
    void addPlan(DataPlanServiceModel dataPlanServiceModel);

    DataPlanEntity findByName(String name);

    List<DataPlanViewModel> findAllPlansOrderedByPrice();

    void deletePlan(String name);

    DataPlanViewModel findById(Long id);

    void updatePlan(DataPlanServiceModel dataPlanServiceModel);
}
