package bg.greencom.greencomwebapp.service;

import bg.greencom.greencomwebapp.model.binding.VoicePlanBindingModel;
import bg.greencom.greencomwebapp.model.entity.VoicePlanEntity;
import bg.greencom.greencomwebapp.model.service.VoicePlanServiceModel;
import bg.greencom.greencomwebapp.model.view.VoicePlanViewModel;

import java.util.List;

public interface VoicePlanService {

    List<VoicePlanViewModel> findAllPlansOrderedByPrice();

    VoicePlanServiceModel addPlan(VoicePlanServiceModel voicePlanServiceModel);

    VoicePlanEntity findByName(String name);

    void deleteVoicePlan(String name);

    void updatePlan(VoicePlanServiceModel voicePlanServiceModel);

    VoicePlanViewModel findById(Long id);
}
