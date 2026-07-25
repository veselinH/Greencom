package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.DataPlanEntity;
import bg.greencom.greencomwebapp.model.entity.MobileExtraEntity;
import bg.greencom.greencomwebapp.model.entity.enums.MobileExtraEnum;
import bg.greencom.greencomwebapp.model.service.DataPlanServiceModel;
import bg.greencom.greencomwebapp.model.view.DataPlanViewModel;
import bg.greencom.greencomwebapp.repository.DataPlanRepository;
import bg.greencom.greencomwebapp.service.MobileExtraService;
import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataPlanServiceImplTest {

    @Mock
    private DataPlanRepository dataPlanRepository;

    @Mock
    private MobileExtraService mobileExtraService;

    private DataPlanServiceImpl dataPlanService;

    @BeforeEach
    void setUp() {
        dataPlanService = new DataPlanServiceImpl(dataPlanRepository, new ModelMapper(), mobileExtraService);
    }

    private DataPlanEntity dataPlanEntity() {
        DataPlanEntity entity = new DataPlanEntity();
        entity.setId(1L);
        entity.setName("Data 50GB");
        entity.setPlanDuration("24");
        entity.setPrice(new BigDecimal("19.99"));
        entity.setBgInternetMegabytes("50000");
        entity.setRoamingInternetMegabytes("2000");
        entity.setActive(true);
        return entity;
    }

    private MobileExtraEntity extra(MobileExtraEnum name) {
        return (MobileExtraEntity) new MobileExtraEntity().setName(name).setId(1L);
    }

    @Test
    void addPlan_mapsAndPersistsActivePlan() {
        DataPlanServiceModel serviceModel = new DataPlanServiceModel();
        serviceModel.setName("Data 50GB");
        serviceModel.setPlanDuration("24");
        serviceModel.setBgInternetMegabytes("50000");
        serviceModel.setPrice(new BigDecimal("19.99"));
        serviceModel.setMobileExtras(List.of(MobileExtraEnum.MOBILE_TV));

        when(mobileExtraService.findByName(MobileExtraEnum.MOBILE_TV))
                .thenReturn(extra(MobileExtraEnum.MOBILE_TV));

        dataPlanService.addPlan(serviceModel);

        ArgumentCaptor<DataPlanEntity> captor = ArgumentCaptor.forClass(DataPlanEntity.class);
        verify(dataPlanRepository).saveAndFlush(captor.capture());

        DataPlanEntity saved = captor.getValue();
        assertEquals("Data 50GB", saved.getName());
        assertTrue(saved.isActive());
        assertNotNull(saved.getCreatedOn());
        assertEquals(1, saved.getMobileExtras().size());
    }

    @Test
    void updatePlan_updatesExistingEntity() {
        DataPlanEntity existing = dataPlanEntity();
        when(dataPlanRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(mobileExtraService.findByName(MobileExtraEnum.MOBILE_MUSIC))
                .thenReturn(extra(MobileExtraEnum.MOBILE_MUSIC));

        DataPlanServiceModel serviceModel = new DataPlanServiceModel();
        serviceModel.setId(1L);
        serviceModel.setName("Data 100GB");
        serviceModel.setPlanDuration("12");
        serviceModel.setBgInternetMegabytes("100000");
        serviceModel.setRoamingInternetMegabytes("5000");
        serviceModel.setMobileExtras(List.of(MobileExtraEnum.MOBILE_MUSIC));
        serviceModel.setActive(true);

        dataPlanService.updatePlan(serviceModel);

        verify(dataPlanRepository).saveAndFlush(existing);
        assertEquals("Data 100GB", existing.getName());
        assertEquals("100000", existing.getBgInternetMegabytes());
        assertNotNull(existing.getModifiedOn());
    }

    @Test
    void updatePlan_throwsWhenPlanDoesNotExist() {
        when(dataPlanRepository.findById(99L)).thenReturn(Optional.empty());

        DataPlanServiceModel serviceModel = new DataPlanServiceModel();
        serviceModel.setId(99L);

        assertThrows(ObjectNotFoundException.class, () -> dataPlanService.updatePlan(serviceModel));
    }

    @Test
    void findAllPlansOrderedByPrice_mapsEntities() {
        when(dataPlanRepository.findAllVoicePlansOrderedByPrice())
                .thenReturn(List.of(dataPlanEntity()));

        List<DataPlanViewModel> result = dataPlanService.findAllPlansOrderedByPrice();

        assertEquals(1, result.size());
        assertEquals("Data 50GB", result.get(0).getName());
    }

    @Test
    void findById_returnsViewModel() {
        when(dataPlanRepository.findById(1L)).thenReturn(Optional.of(dataPlanEntity()));

        DataPlanViewModel result = dataPlanService.findById(1L);

        assertEquals("Data 50GB", result.getName());
    }

    @Test
    void findById_throwsWhenPlanDoesNotExist() {
        when(dataPlanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> dataPlanService.findById(99L));
    }

    @Test
    void findEntityById_returnsEntity() {
        DataPlanEntity entity = dataPlanEntity();
        when(dataPlanRepository.findById(1L)).thenReturn(Optional.of(entity));

        assertSame(entity, dataPlanService.findEntityById(1L));
    }

    @Test
    void findEntityById_throwsWhenPlanDoesNotExist() {
        when(dataPlanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> dataPlanService.findEntityById(99L));
    }
}
