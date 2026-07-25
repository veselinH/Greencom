package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.MobileExtraEntity;
import bg.greencom.greencomwebapp.model.entity.VoicePlanEntity;
import bg.greencom.greencomwebapp.model.entity.enums.MobileExtraEnum;
import bg.greencom.greencomwebapp.model.service.VoicePlanServiceModel;
import bg.greencom.greencomwebapp.model.view.VoicePlanViewModel;
import bg.greencom.greencomwebapp.repository.VoicePlanRepository;
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
class VoicePlanServiceImplTest {

    @Mock
    private VoicePlanRepository voicePlanRepository;

    @Mock
    private MobileExtraService mobileExtraService;

    private VoicePlanServiceImpl voicePlanService;

    @BeforeEach
    void setUp() {
        voicePlanService = new VoicePlanServiceImpl(voicePlanRepository, new ModelMapper(), mobileExtraService);
    }

    private VoicePlanEntity voicePlanEntity() {
        VoicePlanEntity entity = new VoicePlanEntity();
        entity.setId(1L);
        entity.setName("Unlimited 100");
        entity.setPlanDuration("24");
        entity.setPrice(new BigDecimal("39.99"));
        entity.setBgMinutes("Unlimited");
        entity.setRoamingMinutes("100");
        entity.setBgInternetMegabytes("10000");
        entity.setRoamingInternetMegabytes("500");
        entity.setActive(true);
        return entity;
    }

    private MobileExtraEntity extra(MobileExtraEnum name) {
        return (MobileExtraEntity) new MobileExtraEntity().setName(name).setId(1L);
    }

    @Test
    void addPlan_mapsAndPersistsActivePlan() {
        VoicePlanServiceModel serviceModel = new VoicePlanServiceModel()
                .setName("Unlimited 100")
                .setPlanDuration("24")
                .setBgMinutes("Unlimited")
                .setBgInternetMegabytes("10000")
                .setPrice(new BigDecimal("39.99"))
                .setMobileExtras(List.of(MobileExtraEnum.MOBILE_TV));

        when(mobileExtraService.findByName(MobileExtraEnum.MOBILE_TV))
                .thenReturn(extra(MobileExtraEnum.MOBILE_TV));

        voicePlanService.addPlan(serviceModel);

        ArgumentCaptor<VoicePlanEntity> captor = ArgumentCaptor.forClass(VoicePlanEntity.class);
        verify(voicePlanRepository).saveAndFlush(captor.capture());

        VoicePlanEntity saved = captor.getValue();
        assertEquals("Unlimited 100", saved.getName());
        assertTrue(saved.isActive());
        assertNotNull(saved.getCreatedOn());
        assertEquals(1, saved.getMobileExtras().size());
    }

    @Test
    void updatePlan_updatesExistingEntity() {
        VoicePlanEntity existing = voicePlanEntity();
        when(voicePlanRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(mobileExtraService.findByName(MobileExtraEnum.MOBILE_MUSIC))
                .thenReturn(extra(MobileExtraEnum.MOBILE_MUSIC));

        VoicePlanServiceModel serviceModel = new VoicePlanServiceModel()
                .setId(1L)
                .setName("Unlimited 200")
                .setPlanDuration("12")
                .setBgMinutes("Unlimited")
                .setRoamingMinutes("200")
                .setBgInternetMegabytes("20000")
                .setRoamingInternetMegabytes("1000")
                .setMobileExtras(List.of(MobileExtraEnum.MOBILE_MUSIC));
        serviceModel.setActive(true);

        voicePlanService.updatePlan(serviceModel);

        verify(voicePlanRepository).saveAndFlush(existing);
        assertEquals("Unlimited 200", existing.getName());
        assertEquals("12", existing.getPlanDuration());
        assertEquals("200", existing.getRoamingMinutes());
        assertNotNull(existing.getModifiedOn());
        assertEquals(1, existing.getMobileExtras().size());
    }

    @Test
    void updatePlan_throwsWhenPlanDoesNotExist() {
        when(voicePlanRepository.findById(99L)).thenReturn(Optional.empty());

        VoicePlanServiceModel serviceModel = new VoicePlanServiceModel().setId(99L);

        assertThrows(ObjectNotFoundException.class, () -> voicePlanService.updatePlan(serviceModel));
    }

    @Test
    void findAllPlansOrderedByPrice_mapsEntitiesAndSortsExtras() {
        VoicePlanEntity entity = voicePlanEntity();
        entity.setMobileExtras(new java.util.ArrayList<>(List.of(
                extra(MobileExtraEnum.MOBILE_MUSIC),
                extra(MobileExtraEnum.MOBILE_TV))));
        when(voicePlanRepository.findAllVoicePlansOrderedByPrice()).thenReturn(List.of(entity));

        List<VoicePlanViewModel> result = voicePlanService.findAllPlansOrderedByPrice();

        assertEquals(1, result.size());
        assertEquals("Unlimited 100", result.get(0).getName());
        assertEquals(MobileExtraEnum.MOBILE_TV, result.get(0).getMobileExtras().get(0).getName());
    }

    @Test
    void findById_returnsViewModel() {
        when(voicePlanRepository.findById(1L)).thenReturn(Optional.of(voicePlanEntity()));

        VoicePlanViewModel result = voicePlanService.findById(1L);

        assertEquals("Unlimited 100", result.getName());
        assertEquals(new BigDecimal("39.99"), result.getPrice());
    }

    @Test
    void findById_throwsWhenPlanDoesNotExist() {
        when(voicePlanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> voicePlanService.findById(99L));
    }

    @Test
    void findEntityById_returnsEntity() {
        VoicePlanEntity entity = voicePlanEntity();
        when(voicePlanRepository.findById(1L)).thenReturn(Optional.of(entity));

        assertSame(entity, voicePlanService.findEntityById(1L));
    }

    @Test
    void findEntityById_throwsWhenPlanDoesNotExist() {
        when(voicePlanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> voicePlanService.findEntityById(99L));
    }
}
