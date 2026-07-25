package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.TelevisionPlanEntity;
import bg.greencom.greencomwebapp.model.entity.TelevisionTypeEntity;
import bg.greencom.greencomwebapp.model.entity.enums.TelevisionTypeEnum;
import bg.greencom.greencomwebapp.model.service.TelevisionPlanServiceModel;
import bg.greencom.greencomwebapp.model.view.TelevisionPlanViewModel;
import bg.greencom.greencomwebapp.repository.TelevisionPlanRepository;
import bg.greencom.greencomwebapp.repository.TelevisionTypeRepository;
import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelevisionPlanServiceImplTest {

    @Mock
    private TelevisionPlanRepository televisionPlanRepository;

    @Mock
    private TelevisionTypeRepository televisionTypeRepository;

    private TelevisionPlanServiceImpl televisionPlanService;

    @BeforeEach
    void setUp() {
        televisionPlanService = new TelevisionPlanServiceImpl(
                televisionPlanRepository, new ModelMapper(), televisionTypeRepository);
    }

    private TelevisionTypeEntity satelliteType() {
        return (TelevisionTypeEntity) new TelevisionTypeEntity().setName(TelevisionTypeEnum.SATELLITE).setId(1L);
    }

    private TelevisionPlanEntity televisionPlanEntity() {
        TelevisionPlanEntity entity = new TelevisionPlanEntity();
        entity.setId(1L);
        entity.setName("TV Max");
        entity.setPlanDuration("24");
        entity.setPrice(new BigDecimal("14.99"));
        entity.setChannelCount(200);
        entity.setChannelCountInHD(80);
        entity.setTelevisionType(satelliteType());
        entity.setActive(true);
        return entity;
    }

    @Test
    void addPlan_persistsActivePlanWithType() {
        TelevisionPlanServiceModel serviceModel = new TelevisionPlanServiceModel();
        serviceModel.setName("TV Max");
        serviceModel.setPlanDuration("24");
        serviceModel.setPrice(new BigDecimal("14.99"));
        serviceModel.setTelevisionType(TelevisionTypeEnum.SATELLITE);
        serviceModel.setChannelCount(200);
        serviceModel.setChannelCountHD(80);

        when(televisionTypeRepository.findByName(TelevisionTypeEnum.SATELLITE)).thenReturn(satelliteType());

        televisionPlanService.addPlan(serviceModel);

        ArgumentCaptor<TelevisionPlanEntity> captor = ArgumentCaptor.forClass(TelevisionPlanEntity.class);
        verify(televisionPlanRepository).saveAndFlush(captor.capture());

        TelevisionPlanEntity saved = captor.getValue();
        assertEquals("TV Max", saved.getName());
        assertEquals(200, saved.getChannelCount());
        assertEquals(TelevisionTypeEnum.SATELLITE, saved.getTelevisionType().getName());
        assertTrue(saved.isActive());
        assertNotNull(saved.getCreatedOn());
    }

    @Test
    void findAllPlansOrderedByPrice_mapsEntitiesWithTypeName() {
        when(televisionPlanRepository.findAllTelevisionPlansOrderedByPrice())
                .thenReturn(Set.of(televisionPlanEntity()));

        Set<TelevisionPlanViewModel> result = televisionPlanService.findAllPlansOrderedByPrice();

        assertEquals(1, result.size());
        TelevisionPlanViewModel viewModel = result.iterator().next();
        assertEquals("TV Max", viewModel.getName());
        assertEquals("Satellite", viewModel.getTelevisionType());
    }

    @Test
    void findById_returnsViewModelOrNull() {
        when(televisionPlanRepository.findById(1L)).thenReturn(Optional.of(televisionPlanEntity()));
        when(televisionPlanRepository.findById(99L)).thenReturn(Optional.empty());

        assertEquals("TV Max", televisionPlanService.findById(1L).getName());
        assertNull(televisionPlanService.findById(99L));
    }

    @Test
    void findEntityById_returnsEntity() {
        TelevisionPlanEntity entity = televisionPlanEntity();
        when(televisionPlanRepository.findById(1L)).thenReturn(Optional.of(entity));

        assertSame(entity, televisionPlanService.findEntityById(1L));
    }

    @Test
    void findEntityById_throwsWhenPlanDoesNotExist() {
        when(televisionPlanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> televisionPlanService.findEntityById(99L));
    }

    @Test
    void updateTelevisionPlan_updatesExistingEntity() {
        TelevisionPlanEntity existing = televisionPlanEntity();
        when(televisionPlanRepository.findById(1L)).thenReturn(Optional.of(existing));

        TelevisionPlanServiceModel serviceModel = new TelevisionPlanServiceModel();
        serviceModel.setId(1L);
        serviceModel.setName("TV Max");
        serviceModel.setPlanDuration("12");
        serviceModel.setChannelCount(250);
        serviceModel.setActive(true);

        televisionPlanService.updateTelevisionPlan(serviceModel);

        verify(televisionPlanRepository).saveAndFlush(existing);
        assertEquals(250, existing.getChannelCount());
        assertEquals("12", existing.getPlanDuration());
        assertNotNull(existing.getModifiedOn());
    }

    @Test
    void updateTelevisionPlan_throwsWhenPlanDoesNotExist() {
        when(televisionPlanRepository.findById(99L)).thenReturn(Optional.empty());

        TelevisionPlanServiceModel serviceModel = new TelevisionPlanServiceModel();
        serviceModel.setId(99L);

        assertThrows(ObjectNotFoundException.class, () -> televisionPlanService.updateTelevisionPlan(serviceModel));
    }
}
