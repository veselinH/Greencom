package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.TelevisionPlanEntity;
import bg.greencom.greencomwebapp.model.entity.TelevisionTypeEntity;
import bg.greencom.greencomwebapp.model.service.TelevisionPlanServiceModel;
import bg.greencom.greencomwebapp.model.view.AdditionalPackageViewModel;
import bg.greencom.greencomwebapp.model.view.TelevisionPlanViewModel;
import bg.greencom.greencomwebapp.repository.TelevisionPlanRepository;
import bg.greencom.greencomwebapp.repository.TelevisionTypeRepository;
import bg.greencom.greencomwebapp.service.TelevisionPlanService;
import org.hibernate.ObjectNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation managing television subscription plans and business rules.
 */
@Service
public class TelevisionPlanServiceImpl implements TelevisionPlanService {

    private static final String OBJECT_TYPE = "television plan";

    private final TelevisionPlanRepository televisionPlanRepository;
    private final ModelMapper modelMapper;
    private final TelevisionTypeRepository televisionTypeRepository;

    public TelevisionPlanServiceImpl(TelevisionPlanRepository televisionPlanRepository, ModelMapper modelMapper, TelevisionTypeRepository televisionTypeRepository) {
        this.televisionPlanRepository = televisionPlanRepository;
        this.modelMapper = modelMapper;
        this.televisionTypeRepository = televisionTypeRepository;
    }

    /**
     * Retrieves all active television plans, maps them to view models,
     * and sorts their sub-packages using natural comparable ordering.
     */
    @Override
    public Set<TelevisionPlanViewModel> findAllPlansOrderedByPrice() {

        return televisionPlanRepository
                .findAllTelevisionPlansOrderedByPrice()
                .stream()
                .map(televisionPlanEntity -> {
                    TelevisionPlanViewModel viewModel = modelMapper.map(televisionPlanEntity, TelevisionPlanViewModel.class);
                    viewModel.setTelevisionType(televisionPlanEntity.getTelevisionType().getName().getValue());

//                  Sort the additional package extras
                    Set<AdditionalPackageViewModel> sortedExtras = viewModel.getAdditionalPackages()
                            .stream()
                            .sorted() // Uses TelevisionExtras.compareTo()
                            .collect(Collectors.toCollection(LinkedHashSet::new));

                    viewModel.setAdditionalPackages(sortedExtras);

                    return viewModel;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Creates a new active television plan and saves it immediately to the database.
     */
    @Override
    public void addPlan(TelevisionPlanServiceModel televisionPlanServiceModel) {

        TelevisionPlanEntity televisionPlan = new TelevisionPlanEntity();
        TelevisionTypeEntity televisionType = televisionTypeRepository.findByName(televisionPlanServiceModel.getTelevisionType());

        televisionPlan
                .setChannelCount(televisionPlanServiceModel.getChannelCount())
                .setChannelCountInHD(televisionPlanServiceModel.getChannelCountHD())
                .setTelevisionType(televisionType)
                .setName(televisionPlanServiceModel.getName())
                .setPlanDuration(televisionPlanServiceModel.getPlanDuration())
                .setPrice(televisionPlanServiceModel.getPrice())
                .setCreatedOn(LocalDateTime.now())
                .setActive(true);

        televisionPlanRepository.saveAndFlush(televisionPlan);

    }

    /**
     * Finds a television plan by its ID and maps it to a view model,
     * returning null if missing.
     */
    @Override
    public TelevisionPlanViewModel findById(Long id) {
        return televisionPlanRepository.findById(id).map(televisionPlanEntity -> modelMapper.map(televisionPlanEntity, TelevisionPlanViewModel.class)).orElse(null);
    }

    /**
     * Retrieves the raw entity from the database or throws an exception
     * if the requested ID is invalid.
     */
    @Override
    public TelevisionPlanEntity findEntityById(Long planId) {
        return televisionPlanRepository
                .findById(planId)
                .orElseThrow(() -> new ObjectNotFoundException(planId, OBJECT_TYPE));
    }

    /**
     * Updates properties of an existing plan, refreshes the modification
     * timestamp, and persists changes.
     */
    @Override
    public void updateTelevisionPlan(TelevisionPlanServiceModel televisionPlanServiceModel) {
        TelevisionPlanEntity televisionPlanEntity =
                televisionPlanRepository
                        .findById(televisionPlanServiceModel.getId())
                        .orElseThrow(() -> new ObjectNotFoundException(televisionPlanServiceModel.getId(), OBJECT_TYPE));

        televisionPlanEntity
                .setChannelCount(televisionPlanServiceModel.getChannelCount())
                .setChannelCountInHD(televisionPlanEntity.getChannelCountInHD())
                .setModifiedOn(LocalDateTime.now())
                .setPlanDuration(televisionPlanServiceModel.getPlanDuration())
                .setActive(televisionPlanServiceModel.isActive());

        televisionPlanRepository.saveAndFlush(televisionPlanEntity);
    }
}
