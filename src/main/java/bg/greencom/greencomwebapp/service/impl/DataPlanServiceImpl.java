package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.DataPlanEntity;
import bg.greencom.greencomwebapp.model.service.DataPlanServiceModel;
import bg.greencom.greencomwebapp.model.view.DataPlanViewModel;
import bg.greencom.greencomwebapp.repository.DataPlanRepository;
import bg.greencom.greencomwebapp.service.DataPlanService;
import bg.greencom.greencomwebapp.service.MobileExtraService;
import org.hibernate.ObjectNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation managing mobile data plans, gigabyte quotas, and add-on extras.
 */
@Service
public class DataPlanServiceImpl implements DataPlanService {

    private static final String OBJECT_TYPE = "data plan";
    private static final Logger LOGGER = LoggerFactory.getLogger(DataPlanServiceImpl.class);

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
                .setCreatedOn(LocalDateTime.now())
                .setActive(true);

        if (dataPlanServiceModel.getMobileExtras() != null) {
            dataPlanEntity
                    .setMobileExtras(dataPlanServiceModel
                            .getMobileExtras()
                            .stream()
                            .map(mobileExtraService::findByName)
                            .collect(Collectors.toList()));
        }

        dataPlanRepository.saveAndFlush(dataPlanEntity);
        LOGGER.info("Data plan {} added successfully", dataPlanServiceModel.getName());
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
                .setActive(dataPlanServiceModel.isActive());

        dataPlanRepository.saveAndFlush(dataPlan);
        LOGGER.info("Data plan {} updated successfully", dataPlanServiceModel.getName());
    }

    @Override
    public DataPlanEntity findEntityById(Long id) {
        return dataPlanRepository
                .findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id, OBJECT_TYPE));
    }


}
