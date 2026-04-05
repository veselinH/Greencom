package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.InternetPlanEntity;
import bg.greencom.greencomwebapp.model.entity.InternetTypeEntity;
import bg.greencom.greencomwebapp.model.service.InternetPlanServiceModel;
import bg.greencom.greencomwebapp.repository.InternetPlanRepository;
import bg.greencom.greencomwebapp.repository.InternetTypeRepository;
import bg.greencom.greencomwebapp.service.InternetExtraService;
import bg.greencom.greencomwebapp.service.InternetPlanService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class InternetPlanServiceImpl implements InternetPlanService {

    private final InternetExtraService internetExtraService;
    private final InternetPlanRepository internetPlanRepository;
    private final InternetTypeRepository internetTypeRepository;

    public InternetPlanServiceImpl(InternetExtraService internetExtraService, InternetPlanRepository internetPlanRepository, InternetTypeRepository internetTypeRepository) {
        this.internetExtraService = internetExtraService;
        this.internetPlanRepository = internetPlanRepository;
        this.internetTypeRepository = internetTypeRepository;
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
}
