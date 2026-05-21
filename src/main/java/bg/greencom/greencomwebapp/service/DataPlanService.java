package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.entity.DataPlanEntity;
import bg.greencom.greencomwebapp.model.service.DataPlanServiceModel;
import bg.greencom.greencomwebapp.model.view.DataPlanViewModel;

import java.util.List;

public interface DataPlanService {
    void addPlan(DataPlanServiceModel dataPlanServiceModel);

    List<DataPlanViewModel> findAllPlansOrderedByPrice();

    DataPlanViewModel findById(Long id);

    void updatePlan(DataPlanServiceModel dataPlanServiceModel);

    DataPlanEntity findEntityById(Long id);
}
