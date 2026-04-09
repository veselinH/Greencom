package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.DataPlanEntity;
import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.model.exception.ObjectNotFoundException;
import bg.greencom.greencomwebapp.model.service.DataPlanServiceModel;
import bg.greencom.greencomwebapp.model.view.DataPlanViewModel;
import bg.greencom.greencomwebapp.repository.DataPlanRepository;
import bg.greencom.greencomwebapp.repository.UserRepository;
import bg.greencom.greencomwebapp.service.DataPlanService;
import bg.greencom.greencomwebapp.service.MobileExtraService;
import bg.greencom.greencomwebapp.service.PlanService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataPlanServiceImpl implements DataPlanService {

    private static final String OBJECT_TYPE = "data plan";
    private final DataPlanRepository dataPlanRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final MobileExtraService mobileExtraService;
    private final PlanService planService;

    public DataPlanServiceImpl(DataPlanRepository dataPlanRepository, UserRepository userRepository, ModelMapper modelMapper, MobileExtraService mobileExtraService, PlanService planService) {
        this.dataPlanRepository = dataPlanRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.mobileExtraService = mobileExtraService;
        this.planService = planService;
    }


    @Override
    public void addPlan(DataPlanServiceModel dataPlanServiceModel) {

        DataPlanEntity dataPlanEntity = modelMapper.map(dataPlanServiceModel, DataPlanEntity.class);

        dataPlanEntity
                .setCreatedOn(LocalDateTime.now());

        if (dataPlanServiceModel.getMobileExtras() != null) {
            dataPlanEntity
                    .setMobileExtras(dataPlanServiceModel
                            .getMobileExtras()
                            .stream()
                            .map(mobileExtraService::findByName)
                            .collect(Collectors.toList()));
        }

        dataPlanRepository.saveAndFlush(dataPlanEntity);
    }

    @Override
    public DataPlanEntity findByName(String name) {
        return dataPlanRepository
                .findByName(name)
                .orElse(null);
    }

    @Override
    public List<DataPlanViewModel> findAllPlansOrderedByPrice() {
        List<DataPlanViewModel> allDataPlans = dataPlanRepository
                .findAllVoicePlansOrderedByPrice()
                .stream()
                .map(dataPlanEntity -> modelMapper.map(dataPlanEntity, DataPlanViewModel.class))
                .toList();

        for (DataPlanViewModel dataPlan : allDataPlans) {
            Collections.sort(dataPlan.getMobileExtras());
        }

        return allDataPlans;
    }

//    @Override
////  Using transactional in order to delete the plan and the foreign
//    @Transactional
//    public void deletePlan(String name) {
//        DataPlanEntity planToDelete = findByName(name);
//        List<UserEntity> usersWithPlanToDelete = userRepository.findAllByUserDataPlansContains(planToDelete);
//
//        dataPlanRepository.delete(planToDelete);
//    }

    @Override
    public DataPlanViewModel findById(Long id) {
        DataPlanEntity dataPlanEntity = dataPlanRepository
                .findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id, OBJECT_TYPE));

        return modelMapper.map(dataPlanEntity, DataPlanViewModel.class);

    }

    @Override
    public void updatePlan(DataPlanServiceModel dataPlanServiceModel) {

        DataPlanEntity dataPlan =
                dataPlanRepository
                        .findById(dataPlanServiceModel.getId())
                        .orElseThrow(
                                () -> new ObjectNotFoundException(dataPlanServiceModel.getId(), OBJECT_TYPE)
                        );

        dataPlan
                .setName(dataPlanServiceModel.getName())
                .setModifiedOn(LocalDateTime.now())
                .setPlanDuration(dataPlanServiceModel.getPlanDuration());
        dataPlan
                .setBgInternetMegabytes(dataPlanServiceModel.getBgInternetMegabytes())
                .setRoamingInternetMegabytes(dataPlanServiceModel.getRoamingInternetMegabytes())
                .setMobileExtras(dataPlanServiceModel
                        .getMobileExtras()
                        .stream()
                        .map(mobileExtraService::findByName)
                        .collect(Collectors.toList()))
                .setPrice(dataPlanServiceModel.getPrice());

        dataPlanRepository.saveAndFlush(dataPlan);
    }


}
