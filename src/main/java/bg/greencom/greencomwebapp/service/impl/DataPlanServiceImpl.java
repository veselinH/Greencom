package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.DataPlanEntity;
import bg.greencom.greencomwebapp.model.service.DataPlanServiceModel;
import bg.greencom.greencomwebapp.model.view.DataPlanViewModel;
import bg.greencom.greencomwebapp.repository.DataPlanRepository;
import bg.greencom.greencomwebapp.service.DataPlanService;
import bg.greencom.greencomwebapp.service.MobileExtraService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataPlanServiceImpl implements DataPlanService {

    private final DataPlanRepository dataPlanRepository;
    private final ModelMapper modelMapper;
    private final MobileExtraService mobileExtraService;

    public DataPlanServiceImpl(DataPlanRepository dataPlanRepository, ModelMapper modelMapper, MobileExtraService mobileExtraService) {
        this.dataPlanRepository = dataPlanRepository;
        this.modelMapper = modelMapper;
        this.mobileExtraService = mobileExtraService;
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

    @Override
    public void deletePlan(String name) {
        DataPlanEntity planToDelete = findByName(name);
        dataPlanRepository.delete(planToDelete);
    }

    @Override
    public DataPlanViewModel findById(Long id) {
        DataPlanEntity dataPlanEntity = dataPlanRepository
                .findById(id)
                .orElse(null);

        return modelMapper.map(dataPlanEntity, DataPlanViewModel.class);

    }

    @Override
    public void updatePlan(DataPlanServiceModel dataPlanServiceModel) {

        DataPlanEntity dataPlan =
                dataPlanRepository
                        .findById(dataPlanServiceModel.getId())
                        .orElseThrow(
                                () -> new NullPointerException("Voice plan with id " + dataPlanServiceModel.getId() + " does not exist!")
                        );

        dataPlan
                .setName(dataPlanServiceModel.getName())
                .setModifiedOn(LocalDateTime.now())
                .setPlanDuration(dataPlanServiceModel.getPlanDuration());
        dataPlan
                .setBgInternetMegabytes(dataPlanServiceModel.getBgInternetMegabytes())
                .setPrice(dataPlanServiceModel.getPrice())
                .setRoamingInternetMegabytes(dataPlanServiceModel.getRoamingInternetMegabytes())
                .setMobileExtras(dataPlanServiceModel
                        .getMobileExtras()
                        .stream()
                        .map(mobileExtraService::findByName)
                        .collect(Collectors.toList()));

        dataPlanRepository.saveAndFlush(dataPlan);
    }


}
