package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.InternetPlanEntity;
import bg.greencom.greencomwebapp.model.entity.InternetTypeEntity;
import bg.greencom.greencomwebapp.model.entity.enums.InternetTypeEnum;
import bg.greencom.greencomwebapp.model.service.InternetPlanServiceModel;
import bg.greencom.greencomwebapp.model.view.InternetPlanViewModel;
import bg.greencom.greencomwebapp.repository.InternetPlanRepository;
import bg.greencom.greencomwebapp.repository.InternetTypeRepository;
import bg.greencom.greencomwebapp.service.InternetExtraService;
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
class InternetPlanServiceImplTest {

    @Mock
    private InternetExtraService internetExtraService;

    @Mock
    private InternetPlanRepository internetPlanRepository;

    @Mock
    private InternetTypeRepository internetTypeRepository;

    private InternetPlanServiceImpl internetPlanService;

    @BeforeEach
    void setUp() {
        internetPlanService = new InternetPlanServiceImpl(
                internetExtraService, internetPlanRepository, internetTypeRepository, new ModelMapper());
    }

    private InternetTypeEntity fiberType() {
        return (InternetTypeEntity) new InternetTypeEntity().setName(InternetTypeEnum.FIBER_NET).setId(1L);
    }

    private InternetPlanEntity internetPlanEntity() {
        InternetPlanEntity entity = new InternetPlanEntity();
        entity.setId(1L);
        entity.setName("Fiber 300");
        entity.setPlanDuration("24");
        entity.setPrice(new BigDecimal("24.99"));
        entity.setDownloadMbps(300);
        entity.setUploadMbps(100);
        entity.setInternetType(fiberType());
        entity.setInternetExtras(new java.util.HashSet<>());
        entity.setActive(true);
        return entity;
    }

    @Test
    void addPlan_persistsActivePlanWithType() {
        InternetPlanServiceModel serviceModel = new InternetPlanServiceModel();
        serviceModel.setName("Fiber 300");
        serviceModel.setPlanDuration("24");
        serviceModel.setDownloadMbps(300);
        serviceModel.setUploadMbps(100);
        serviceModel.setPrice(new BigDecimal("24.99"));
        serviceModel.setInternetType(InternetTypeEnum.FIBER_NET);
        serviceModel.setInternetExtras(null);

        when(internetTypeRepository.findByName(InternetTypeEnum.FIBER_NET)).thenReturn(fiberType());

        internetPlanService.addPlan(serviceModel);

        ArgumentCaptor<InternetPlanEntity> captor = ArgumentCaptor.forClass(InternetPlanEntity.class);
        verify(internetPlanRepository).saveAndFlush(captor.capture());

        InternetPlanEntity saved = captor.getValue();
        assertEquals("Fiber 300", saved.getName());
        assertEquals(300, saved.getDownloadMbps());
        assertEquals(InternetTypeEnum.FIBER_NET, saved.getInternetType().getName());
        assertTrue(saved.isActive());
        assertNotNull(saved.getCreatedOn());
    }

    @Test
    void findAllPlansOrderedByPrice_mapsEntitiesWithTypeName() {
        when(internetPlanRepository.findAllInternetPlansOrderedByPrice())
                .thenReturn(List.of(internetPlanEntity()));

        List<InternetPlanViewModel> result = internetPlanService.findAllPlansOrderedByPrice();

        assertEquals(1, result.size());
        assertEquals("Fiber 300", result.get(0).getName());
        assertEquals("FiberNet", result.get(0).getInternetType());
    }

    @Test
    void findById_returnsViewModel() {
        when(internetPlanRepository.findById(1L)).thenReturn(Optional.of(internetPlanEntity()));

        InternetPlanViewModel result = internetPlanService.findById(1L);

        assertEquals("Fiber 300", result.getName());
        assertEquals("FiberNet", result.getInternetType());
    }

    @Test
    void findById_throwsWhenPlanDoesNotExist() {
        when(internetPlanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> internetPlanService.findById(99L));
    }

    @Test
    void updateInternetPlan_updatesExistingEntity() {
        InternetPlanEntity existing = internetPlanEntity();
        when(internetPlanRepository.findById(1L)).thenReturn(Optional.of(existing));

        InternetPlanServiceModel serviceModel = new InternetPlanServiceModel();
        serviceModel.setId(1L);
        serviceModel.setName("Fiber 600");
        serviceModel.setPlanDuration("12");
        serviceModel.setDownloadMbps(600);
        serviceModel.setUploadMbps(200);
        serviceModel.setActive(true);

        internetPlanService.updateInternetPlan(serviceModel);

        verify(internetPlanRepository).saveAndFlush(existing);
        assertEquals(600, existing.getDownloadMbps());
        assertEquals("12", existing.getPlanDuration());
        assertNotNull(existing.getModifiedOn());
    }

    @Test
    void updateInternetPlan_throwsWhenPlanDoesNotExist() {
        when(internetPlanRepository.findById(99L)).thenReturn(Optional.empty());

        InternetPlanServiceModel serviceModel = new InternetPlanServiceModel();
        serviceModel.setId(99L);

        assertThrows(ObjectNotFoundException.class, () -> internetPlanService.updateInternetPlan(serviceModel));
    }

    @Test
    void findEntityById_returnsEntityOrNull() {
        InternetPlanEntity entity = internetPlanEntity();
        when(internetPlanRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(internetPlanRepository.findById(99L)).thenReturn(Optional.empty());

        assertSame(entity, internetPlanService.findEntityById(1L));
        assertNull(internetPlanService.findEntityById(99L));
    }
}
