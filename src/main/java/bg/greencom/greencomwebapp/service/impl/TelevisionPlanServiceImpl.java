package bg.greencom.greencomwebapp.service.impl;


import bg.greencom.greencomwebapp.model.entity.AdditionalPackageEntity;
import bg.greencom.greencomwebapp.model.entity.TelevisionPlanEntity;
import bg.greencom.greencomwebapp.model.entity.TelevisionTypeEntity;
import bg.greencom.greencomwebapp.model.exception.ObjectNotFoundException;
import bg.greencom.greencomwebapp.model.service.TelevisionPlanServiceModel;
import bg.greencom.greencomwebapp.model.view.TelevisionPlanViewModel;
import bg.greencom.greencomwebapp.repository.TelevisionPlanRepository;
import bg.greencom.greencomwebapp.repository.TelevisionTypeRepository;
import bg.greencom.greencomwebapp.service.TelevisionPlanService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    public Set<TelevisionPlanViewModel> findAllPlansOrderedByPrice() {

        return televisionPlanRepository
                .findAllTelevisionPlansOrderedByPrice()
                .stream()
                .map(televisionPlanEntity -> {
                    TelevisionPlanViewModel viewModel = modelMapper.map(televisionPlanEntity, TelevisionPlanViewModel.class);
                    viewModel.setTelevisionType(televisionPlanEntity.getTelevisionType().getName().getValue());

//                  Sort the additional package extras
                    Set<AdditionalPackageEntity> sortedExtras = viewModel.getAdditionalPackageEntities()
                            .stream()
                            .sorted() // Uses TelevisionExtras.compareTo()
                            .collect(Collectors.toCollection(LinkedHashSet::new));

                    viewModel.setAdditionalPackageEntities(sortedExtras);

                    return viewModel;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

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
                .setCreatedOn(LocalDateTime.now());

        televisionPlanRepository.saveAndFlush(televisionPlan);

    }

    @Override
    public TelevisionPlanViewModel findById(Long id) {
        return televisionPlanRepository.findById(id).map(televisionPlanEntity -> modelMapper.map(televisionPlanEntity, TelevisionPlanViewModel.class)).orElse(null);
    }

    @Override
    public TelevisionPlanEntity findEntityById(Long planId) {
        return televisionPlanRepository
                .findById(planId)
                .orElseThrow(() -> new ObjectNotFoundException(planId, OBJECT_TYPE));
    }
}
