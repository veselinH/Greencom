package bg.greencom.greencomwebapp.service.impl;
import bg.greencom.greencomwebapp.model.entity.VoicePlanEntity;
import bg.greencom.greencomwebapp.model.exception.ObjectNotFoundException;
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

/**
 * Service implementation managing operations related to mobile voice subscription plans
 */
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

    /**
     * Retrieves all voice plans from the database ordered by price.
     * Elements within the nested mobile extras collection are sorted to ensure consistent view ordering.
     *
     * @return List of sorted VoicePlanViewModel objects.
     */
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

    /**
     * Contextually provisions a new mobile voice plan in the inventory, tracking initialization
     * metadata and linking associated extra add-ons.
     *
     * @param voicePlanServiceModel The data model containing technical parameters of the new plan.
     * @return The original input service model.
     */
    @Override
    public VoicePlanServiceModel addPlan(VoicePlanServiceModel voicePlanServiceModel) {

        VoicePlanEntity voicePlanEntity = modelMapper.map(voicePlanServiceModel, VoicePlanEntity.class);


        voicePlanEntity
                .setCreatedOn(LocalDateTime.now())
                .setActive(true);


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

    /**
     * Modifies the operational attributes, minutes/data allowances, and extra features
     * of an existing voice subscription framework.
     *
     * @param voicePlanServiceModel Model containing updated properties and the target identifier.
     * @throws ObjectNotFoundException If the target plan instance cannot be found in persistence.
     */
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
                .setRoamingInternetMegabytes(voicePlanServiceModel.getRoamingInternetMegabytes())
                .setMobileExtras(voicePlanServiceModel
                        .getMobileExtras()
                        .stream()
                        .map(mobileExtraService::findByName)
                        .collect(Collectors.toList()))
                .setActive(voicePlanServiceModel.isActive());

        voicePlanRepository.saveAndFlush(voicePlan);
    }

    /**
     * Locates a voice subscription plan and projects its database payload onto a user-facing view format.
     *
     * @param id The unique database identifier of the target plan.
     * @return   The mapped VoicePlanViewModel object representation.
     * @throws ObjectNotFoundException If no matching entity is located.
     */
    @Override
    public VoicePlanViewModel findById(Long id) {
        VoicePlanEntity voicePlanEntity = voicePlanRepository
                .findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id, OBJECT_TYPE));

        return modelMapper.map(voicePlanEntity, VoicePlanViewModel.class);
    }

    /**
     * Locates and exposes a raw database entity for internal transactional manipulation within the system.
     *
     * @param id The unique database identifier of the target plan.
     * @return   The persistent VoicePlanEntity managed instance.
     * @throws ObjectNotFoundException If no matching entity is located.
     */
    @Override
    public VoicePlanEntity findEntityById(Long id) {

        return voicePlanRepository
                .findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id, OBJECT_TYPE));
    }
}
