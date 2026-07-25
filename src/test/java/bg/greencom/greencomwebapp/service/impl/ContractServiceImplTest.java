package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.client.LoyaltyFacade;
import bg.greencom.greencomwebapp.model.entity.AdditionalPackageEntity;
import bg.greencom.greencomwebapp.model.entity.ContractEntity;
import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.model.entity.VoicePlanEntity;
import bg.greencom.greencomwebapp.model.entity.enums.AdditionalPackageEnum;
import bg.greencom.greencomwebapp.model.view.ContractViewModel;
import bg.greencom.greencomwebapp.repository.ContractRepository;
import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContractServiceImplTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private LoyaltyFacade loyaltyFacade;

    private ContractServiceImpl contractService;

    @BeforeEach
    void setUp() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        contractService = new ContractServiceImpl(
                contractRepository, new ModelMapper(), templateEngine, loyaltyFacade);
    }

    private UserEntity user() {
        UserEntity user = new UserEntity();
        user.setUsername("ivan");
        user.setFirstName("Ivan");
        user.setLastName("Ivanov");
        return user;
    }

    private VoicePlanEntity plan() {
        VoicePlanEntity plan = new VoicePlanEntity();
        plan.setId(5L);
        plan.setName("Unlimited 100");
        plan.setPlanDuration("24");
        plan.setPrice(new BigDecimal("39.99"));
        plan.setBgMinutes("Unlimited");
        plan.setBgInternetMegabytes("10000");
        return plan;
    }

    private ContractEntity contract() {
        ContractEntity contract = new ContractEntity()
                .setUser(user())
                .setPlan(plan())
                .setSignedOn(LocalDate.now().minusMonths(2));
        contract.setId(10L);
        contract.setActive(true);
        return contract;
    }

    @Test
    void addContract_persistsActiveContractAndAwardsPoints() {
        contractService.addContract(plan(), user(), null, new byte[]{1, 2});

        ArgumentCaptor<ContractEntity> captor = ArgumentCaptor.forClass(ContractEntity.class);
        verify(contractRepository).saveAndFlush(captor.capture());

        ContractEntity saved = captor.getValue();
        assertTrue(saved.isActive());
        assertEquals(LocalDate.now(), saved.getSignedOn());
        assertArrayEquals(new byte[]{1, 2}, saved.getSignSignature());
        verify(loyaltyFacade).earn("ivan", 40);
    }

    @Test
    void findById_mapsContractWithAdditionalPackages() {
        ContractEntity contract = contract();
        contract.setAdditionalPackageEntities(Set.of(new AdditionalPackageEntity()
                .setName(AdditionalPackageEnum.SPORT_XTRA)
                .setPrice(new BigDecimal("5.00"))));
        when(contractRepository.findById(10L)).thenReturn(Optional.of(contract));

        ContractViewModel result = contractService.findById(10L);

        assertEquals(5L, result.getPlanId());
        assertEquals(1, result.getAdditionalPackageViewModels().size());
    }

    @Test
    void findById_throwsWhenContractDoesNotExist() {
        when(contractRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> contractService.findById(99L));
    }

    @Test
    void deactivateContract_marksInactiveAndRevokesPoints() {
        ContractEntity contract = contract();
        when(contractRepository.findById(10L)).thenReturn(Optional.of(contract));

        contractService.deactivateContract(10L, new byte[]{3});

        assertFalse(contract.isActive());
        assertEquals(LocalDate.now(), contract.getUnsignedOn());
        assertArrayEquals(new byte[]{3}, contract.getUnsignSignature());
        verify(contractRepository).saveAndFlush(contract);
        verify(loyaltyFacade).revoke("ivan", 40);
    }

    @Test
    void generateContractPdf_rendersPdfDocument() {
        when(contractRepository.findById(10L)).thenReturn(Optional.of(contract()));

        byte[] pdf = contractService.generateContractPdf(10L);

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
        assertEquals('%', pdf[0]);
        assertEquals('P', pdf[1]);
        assertEquals('D', pdf[2]);
        assertEquals('F', pdf[3]);
    }

    @Test
    void generateContractPdf_throwsWhenContractDoesNotExist() {
        when(contractRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> contractService.generateContractPdf(99L));
    }

    @Test
    void getContractDownloadFileName_sanitizesPlanName() {
        ContractEntity contract = contract();
        contract.getPlan().setName("Unlimited 100+");
        when(contractRepository.findById(10L)).thenReturn(Optional.of(contract));

        String fileName = contractService.getContractDownloadFileName(10L);

        assertEquals("Unlimited_100__10_" + LocalDate.now() + ".pdf", fileName);
    }

    @Test
    void isContractOwner_checksContractUser() {
        when(contractRepository.findById(10L)).thenReturn(Optional.of(contract()));
        when(contractRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(contractService.isContractOwner(10L, "ivan"));
        assertFalse(contractService.isContractOwner(10L, "other"));
        assertFalse(contractService.isContractOwner(99L, "ivan"));
    }
}
