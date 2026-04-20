package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.InternetPlanEntity;
import bg.greencom.greencomwebapp.model.entity.InternetTypeEntity;
import bg.greencom.greencomwebapp.model.exception.ObjectNotFoundException;
import bg.greencom.greencomwebapp.model.service.InternetPlanServiceModel;
import bg.greencom.greencomwebapp.model.view.InternetPlanViewModel;
import bg.greencom.greencomwebapp.repository.InternetPlanRepository;
import bg.greencom.greencomwebapp.repository.InternetTypeRepository;
import bg.greencom.greencomwebapp.service.InternetExtraService;
import bg.greencom.greencomwebapp.service.InternetPlanService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InternetPlanServiceImpl implements InternetPlanService {

    private static final String OBJECT_TYPE = "internet plan";

    private final InternetExtraService internetExtraService;
    private final InternetPlanRepository internetPlanRepository;
    private final InternetTypeRepository internetTypeRepository;
    private final ModelMapper modelMapper;

    public InternetPlanServiceImpl(InternetExtraService internetExtraService, InternetPlanRepository internetPlanRepository, InternetTypeRepository internetTypeRepository, ModelMapper modelMapper) {
        this.internetExtraService = internetExtraService;
        this.internetPlanRepository = internetPlanRepository;
        this.internetTypeRepository = internetTypeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void addPlan(InternetPlanServiceModel internetPlanServiceModel) {

        InternetPlanEntity internetPlanEntity = new InternetPlanEntity();
        InternetTypeEntity internetType = internetTypeRepository.findByName(internetPlanServiceModel.getInternetType());

        internetType.setName(internetPlanServiceModel.getInternetType());
        internetPlanEntity.setCreatedOn(LocalDateTime.now());

        internetPlanEntity
                .setDownloadMbps(internetPlanServiceModel.getDownloadMbps())
                .setUploadMbps(internetPlanServiceModel.getUploadMbps())
                .setInternetType(internetType)
                .setPrice(internetPlanServiceModel.getPrice())
                .setName(internetPlanServiceModel.getName())
                .setPlanDuration(internetPlanServiceModel.getPlanDuration());

        if (internetPlanServiceModel.getInternetExtras() != null) {
            internetPlanEntity
                    .setInternetExtras(internetPlanServiceModel
                            .getInternetExtras()
                            .stream()
                            .map(internetExtraService::findByName)
                            .collect(Collectors.toSet()));
        }

        internetPlanRepository.saveAndFlush(internetPlanEntity);

    }

    @Override
    public List<InternetPlanViewModel> findAllPlansOrderedByPrice() {
        List<InternetPlanViewModel> allInternetPlans = internetPlanRepository
                .findAllInternetPlansOrderedByPrice()
                .stream()
                .map(internetPlanEntity ->{
                    InternetPlanViewModel viewModel = modelMapper.map(internetPlanEntity, InternetPlanViewModel.class);
                    viewModel.setInternetType(internetPlanEntity.getInternetType().getName().getValue());
                    return viewModel;
                }
                ).toList();

        for (InternetPlanViewModel internetPlan : allInternetPlans){
            Collections.sort(internetPlan.getInternetExtras());
        }
        return allInternetPlans;
    }

    @Override
    public InternetPlanViewModel findById(Long id) {
        InternetPlanEntity internetPlanEntity = internetPlanRepository
                .findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id, OBJECT_TYPE));

        InternetPlanViewModel internetPlanViewModel = modelMapper.map(internetPlanEntity, InternetPlanViewModel.class);
        internetPlanViewModel.setInternetType(internetPlanEntity.getInternetType().getName().getValue());

        return internetPlanViewModel;
    }

    @Override
    public void updateInternetPlan(InternetPlanServiceModel internetPlanServiceModel) {

        InternetPlanEntity internetPlanEntity =
                internetPlanRepository
                        .findById(internetPlanServiceModel.getId())
                        .orElseThrow(() -> new ObjectNotFoundException(internetPlanServiceModel.getId(), OBJECT_TYPE));

        internetPlanEntity
                .setDownloadMbps(internetPlanServiceModel.getDownloadMbps())
                .setUploadMbps(internetPlanServiceModel.getUploadMbps())
                .setPlanDuration(internetPlanServiceModel.getPlanDuration())
                .setPrice(internetPlanServiceModel.getPrice())
                .setName(internetPlanServiceModel.getName());

        internetPlanRepository.saveAndFlush(internetPlanEntity);

    }
}
