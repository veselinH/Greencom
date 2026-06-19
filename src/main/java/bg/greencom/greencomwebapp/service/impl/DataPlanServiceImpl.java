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
    private final DataPlanRepository dataPlanRepository;
    private final ModelMapper modelMapper;
    private final MobileExtraService mobileExtraService;

    public DataPlanServiceImpl(DataPlanRepository dataPlanRepository, ModelMapper modelMapper, MobileExtraService mobileExtraService) {
        this.dataPlanRepository = dataPlanRepository;
        this.modelMapper = modelMapper;
        this.mobileExtraService = mobileExtraService;
    }

    /**
     * Creates and stores a new active mobile data subscription configuration,
     * mapping incoming data and linking associated mobile extras.
     */
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
    }

    /**
     * Fetches all records ordered by base price, converts them into projection
     * views, and applies natural sorting to their individual extra additions.
     */
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

    /**
     * Resolves a single plan layout view based on its distinct entity database key.
     * Throws an exception if the entity record cannot be located.
     */
    @Override
    public DataPlanViewModel findById(Long id) {
        DataPlanEntity dataPlanEntity = dataPlanRepository
                .findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id, OBJECT_TYPE));

        return modelMapper.map(dataPlanEntity, DataPlanViewModel.class);

    }

    /**
     * Updates an existing database plan state with the incoming model attributes,
     * maps updated extra fields, and updates the tracking modification date.
     */
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
    }

    /**
     * Gets the core entity representation directly from database tables,
     * failing with an exception if it is missing.
     */
    @Override
    public DataPlanEntity findEntityById(Long id) {
        return dataPlanRepository
                .findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(id, OBJECT_TYPE));
    }


}
