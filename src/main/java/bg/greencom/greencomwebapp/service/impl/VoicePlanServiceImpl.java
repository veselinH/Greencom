package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.binding.VoicePlanBindingModel;
import bg.greencom.greencomwebapp.model.entity.MobileExtraEntity;
import bg.greencom.greencomwebapp.model.entity.VoicePlanEntity;
import bg.greencom.greencomwebapp.model.exception.ObjectNotFoundException;
import bg.greencom.greencomwebapp.model.exception.PlanNotFoundException;
import bg.greencom.greencomwebapp.model.service.VoicePlanServiceModel;
import bg.greencom.greencomwebapp.model.view.VoicePlanViewModel;
import bg.greencom.greencomwebapp.repository.VoicePlanRepository;
import bg.greencom.greencomwebapp.service.MobileExtraService;
import bg.greencom.greencomwebapp.service.VoicePlanService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VoicePlanServiceImpl implements VoicePlanService {

    private static final String OBJECT_TYPE = "voice plan";

    private final VoicePlanRepository voicePlanRepository;
    private final ModelMapper modelMapper;
    private final MobileExtraService mobileExtraService;

    public VoicePlanServiceImpl(VoicePlanRepository voicePlanRepository, ModelMapper modelMapper, MobileExtraService mobileExtraService) {
        this.voicePlanRepository = voicePlanRepository;
        this.modelMapper = modelMapper;
        this.mobileExtraService = mobileExtraService;
    }


    @Override
    public List<VoicePlanViewModel> findAllPlansOrderedByPrice() {
        List<VoicePlanViewModel> allVoicePlans = voicePlanRepository
                .findAllVoicePlansOrderedByPrice()
                .stream()
                .map(voicePlanEntity -> modelMapper.map(voicePlanEntity, VoicePlanViewModel.class)).toList();

        for (VoicePlanViewModel voicePlan : allVoicePlans) {
            Collections.sort(voicePlan.getMobileExtras());
        }
        return allVoicePlans;
    }

    @Override
    public VoicePlanServiceModel addPlan(VoicePlanServiceModel voicePlanServiceModel) {

        VoicePlanEntity voicePlanEntity = modelMapper.map(voicePlanServiceModel, VoicePlanEntity.class);


        voicePlanEntity
                .setCreatedOn(LocalDateTime.now());


        if (voicePlanServiceModel.getMobileExtras() != null) {
            voicePlanEntity
                    .setMobileExtras(voicePlanServiceModel
                            .getMobileExtras()
                            .stream()
                            .map(mobileExtraService::findByName)
                            .collect(Collectors.toList()));
        }


        voicePlanRepository.saveAndFlush(voicePlanEntity);

        return voicePlanServiceModel;
    }

    @Override
    public VoicePlanEntity findByName(String name) {
        return voicePlanRepository
                .findByName(name)
                .orElse(null);
    }

    @Override
    public void deleteVoicePlan(String name) {

        VoicePlanEntity planToDelete = findByName(name);
        voicePlanRepository.delete(planToDelete);
    }

    @Override
    public void updatePlan(VoicePlanServiceModel voicePlanServiceModel) {

        VoicePlanEntity voicePlan =
                voicePlanRepository
                        .findById(voicePlanServiceModel.getId())
                        .orElseThrow(
                                () -> new ObjectNotFoundException(voicePlanServiceModel.getId(), OBJECT_TYPE));


        voicePlan
                .setName(voicePlanServiceModel.getName())
                .setModifiedOn(LocalDateTime.now())
                .setPlanDuration(voicePlanServiceModel.getPlanDuration());
        voicePlan
                .setBgMinutes(voicePlanServiceModel.getBgMinutes())
                .setRoamingMinutes(voicePlanServiceModel.getRoamingMinutes())
                .setBgInternetMegabytes(voicePlanServiceModel.getBgInternetMegabytes())
                .setPrice(voicePlanServiceModel.getPrice())
                .setRoamingInternetMegabytes(voicePlanServiceModel.getRoamingInternetMegabytes())
                .setMobileExtras(voicePlanServiceModel
                        .getMobileExtras()
                        .stream()
                        .map(mobileExtraService::findByName)
                        .collect(Collectors.toList()));

        voicePlanRepository.saveAndFlush(voicePlan);
    }

    @Override
    public VoicePlanViewModel findById(Long id) {
        VoicePlanEntity voicePlanEntity = voicePlanRepository
                .findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id, OBJECT_TYPE));

        return modelMapper.map(voicePlanEntity, VoicePlanViewModel.class);
    }

}
