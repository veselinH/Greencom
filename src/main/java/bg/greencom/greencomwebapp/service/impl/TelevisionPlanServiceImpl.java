package bg.greencom.greencomwebapp.service.impl;


import bg.greencom.greencomwebapp.model.entity.AdditionalPackageEntity;
import bg.greencom.greencomwebapp.model.view.TelevisionPlanViewModel;
import bg.greencom.greencomwebapp.repository.TelevisionPlanRepository;
import bg.greencom.greencomwebapp.service.TelevisionPlanService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TelevisionPlanServiceImpl implements TelevisionPlanService {

    private final TelevisionPlanRepository televisionPlanRepository;
    private final ModelMapper modelMapper;

    public TelevisionPlanServiceImpl(TelevisionPlanRepository televisionPlanRepository, ModelMapper modelMapper) {
        this.televisionPlanRepository = televisionPlanRepository;
        this.modelMapper = modelMapper;
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
}
