package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.client.LoyaltyException;
import bg.greencom.greencomwebapp.client.LoyaltyFacade;
import bg.greencom.greencomwebapp.client.dto.LoyaltyResponse;
import bg.greencom.greencomwebapp.model.binding.UserProfileEditBindingModel;
import bg.greencom.greencomwebapp.model.entity.AdditionalPackageEntity;
import bg.greencom.greencomwebapp.model.entity.DataPlanEntity;
import bg.greencom.greencomwebapp.model.entity.InternetPlanEntity;
import bg.greencom.greencomwebapp.model.entity.TelevisionPlanEntity;
import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.model.entity.UserRoleEntity;
import bg.greencom.greencomwebapp.model.entity.VoicePlanEntity;
import bg.greencom.greencomwebapp.model.entity.enums.AdditionalPackageEnum;
import bg.greencom.greencomwebapp.model.entity.enums.UserRoleEnum;
import bg.greencom.greencomwebapp.model.service.UserServiceModel;
import bg.greencom.greencomwebapp.model.user.GreencomUserDetails;
import bg.greencom.greencomwebapp.model.view.AdditionalPackageViewModel;
import bg.greencom.greencomwebapp.model.view.ContractViewModel;
import bg.greencom.greencomwebapp.model.view.DataPlanViewModel;
import bg.greencom.greencomwebapp.model.view.InternetPlanViewModel;
import bg.greencom.greencomwebapp.model.view.PlanViewModel;
import bg.greencom.greencomwebapp.model.view.UserViewModel;
import bg.greencom.greencomwebapp.model.view.VoicePlanViewModel;
import bg.greencom.greencomwebapp.repository.UserRepository;
import bg.greencom.greencomwebapp.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserRoleService userRoleService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private VoicePlanService voicePlanService;
    @Mock
    private DataPlanService dataPlanService;
    @Mock
    private PlanService planService;
    @Mock
    private ContractService contractService;
    @Mock
    private InternetPlanService internetPlanService;
    @Mock
    private TelevisionPlanService televisionPlanService;
    @Mock
    private AdditionalPackageService additionalPackageService;
    @Mock
    private LoyaltyFacade loyaltyFacade;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setUsername("test_user");
        testUser.setEmail("test@example.com");
        testUser.setTotalDebtPerMonth(BigDecimal.TEN);
        testUser.setUserVoiceMobilePlans(new java.util.ArrayList<>());
        testUser.setUserDataPlans(new java.util.ArrayList<>());
        testUser.setUserInternetPlans(new java.util.ArrayList<>());
        testUser.setUserTelevisionPlans(new java.util.ArrayList<>());
    }

    private GreencomUserDetails userDetails() {
        return new GreencomUserDetails("test_user", "pass", "test@example.com",
                List.of(new SimpleGrantedAuthority("ROLE_USER")), "Testov");
    }

    private void mockUserFound() {
        when(userRepository.findByUsername("test_user")).thenReturn(Optional.of(testUser));
    }

    @Test
    void testFindUserByUsername_ShouldReturnUser_WhenUserExists() {

        when(userRepository.findByUsername("test_user"))
                .thenReturn(Optional.of(testUser));

        UserEntity result = userService.findUserByUsername("test_user");

        assertNotNull(result);
        assertEquals("test_user", result.getUsername());
    }

    @Test
    void testFindUserByUsername_ShouldReturnNull_WhenUserDoesNotExist() {

        when(userRepository.findByUsername("non_existent")).thenReturn(Optional.empty());

        UserEntity result = userService.findUserByUsername("non_existent");

        assertNull(result);
    }

    @Test
    void testInitialize_ShouldSaveAdmin_WhenRepositoryIsEmpty() {

        when(userRepository.count()).thenReturn(0L);
        when(userRoleService.findByName(any(UserRoleEnum.class))).thenReturn(new UserRoleEntity());
        when(passwordEncoder.encode(any())).thenReturn("hashed_pass");

        userService.initialize();

        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testInitialize_ShouldDoNothing_WhenUsersExist() {

        when(userRepository.count()).thenReturn(5L);

        userService.initialize();

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testFindUserByEmail_ShouldReturnUser_WhenUserExists() {

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        assertSame(testUser, userService.findUserByEmail("test@example.com"));
    }

    @Test
    void testRegisterUser_ShouldPersistUserAndAutoLogin() {

        UserServiceModel serviceModel = new UserServiceModel()
                .setUsername("test_user")
                .setPassword("plain")
                .setEmail("test@example.com");

        when(modelMapper.map(serviceModel, UserEntity.class)).thenReturn(testUser);
        when(userRoleService.findByName(UserRoleEnum.USER)).thenReturn(new UserRoleEntity().setName(UserRoleEnum.USER));
        when(passwordEncoder.encode("plain")).thenReturn("hashed");
        when(userDetailsService.loadUserByUsername("test_user"))
                .thenReturn(new User("test_user", "hashed", List.of(new SimpleGrantedAuthority("ROLE_USER"))));

        AtomicReference<Authentication> loggedIn = new AtomicReference<>();
        userService.registerUser(serviceModel, loggedIn::set);

        verify(userRepository).save(testUser);
        assertEquals("hashed", testUser.getPassword());
        assertNotNull(testUser.getRegisteredOn());
        assertEquals(BigDecimal.ZERO, testUser.getTotalDebtPerMonth());
        assertNotNull(loggedIn.get());
        assertEquals("test_user", ((User) loggedIn.get().getPrincipal()).getUsername());
    }

    @Test
    void testSignVoicePlan_ShouldAddPlanAndIncreaseDebt() {

        mockUserFound();
        VoicePlanEntity voicePlan = new VoicePlanEntity();
        voicePlan.setId(1L);
        voicePlan.setName("Unlimited 100");
        voicePlan.setPrice(new BigDecimal("39.99"));
        when(voicePlanService.findEntityById(1L)).thenReturn(voicePlan);

        userService.signVoicePlan(new VoicePlanViewModel().setId(1L), userDetails(), new byte[]{1});

        assertTrue(testUser.getUserVoiceMobilePlans().contains(voicePlan));
        assertEquals(new BigDecimal("49.99"), testUser.getTotalDebtPerMonth());
        verify(userRepository).saveAndFlush(testUser);
        verify(contractService).addContract(eq(voicePlan), eq(testUser), isNull(), any());
    }

    @Test
    void testSignDataPlan_ShouldAddPlanAndIncreaseDebt() {

        mockUserFound();
        DataPlanEntity dataPlan = new DataPlanEntity();
        dataPlan.setId(2L);
        dataPlan.setName("Data 50GB");
        dataPlan.setPrice(new BigDecimal("19.99"));
        when(dataPlanService.findEntityById(2L)).thenReturn(dataPlan);

        DataPlanViewModel viewModel = new DataPlanViewModel();
        viewModel.setId(2L);
        userService.signDataPlan(viewModel, userDetails(), new byte[]{1});

        assertTrue(testUser.getUserDataPlans().contains(dataPlan));
        assertEquals(new BigDecimal("29.99"), testUser.getTotalDebtPerMonth());
        verify(contractService).addContract(eq(dataPlan), eq(testUser), isNull(), any());
    }

    @Test
    void testSignInternetPlan_ShouldAddPlanAndIncreaseDebt() {

        mockUserFound();
        InternetPlanEntity internetPlan = new InternetPlanEntity();
        internetPlan.setId(3L);
        internetPlan.setName("Fiber 300");
        internetPlan.setPrice(new BigDecimal("24.99"));
        when(internetPlanService.findEntityById(3L)).thenReturn(internetPlan);

        InternetPlanViewModel viewModel = new InternetPlanViewModel();
        viewModel.setId(3L);
        userService.signInternetPlan(viewModel, userDetails(), new byte[]{1});

        assertTrue(testUser.getUserInternetPlans().contains(internetPlan));
        assertEquals(new BigDecimal("34.99"), testUser.getTotalDebtPerMonth());
        verify(contractService).addContract(eq(internetPlan), eq(testUser), isNull(), any());
    }

    @Test
    void testSignTelevisionPlan_ShouldAddPlanAndPackagesAndIncreaseDebt() {

        mockUserFound();
        TelevisionPlanEntity televisionPlan = new TelevisionPlanEntity();
        televisionPlan.setId(4L);
        televisionPlan.setName("TV Max");
        televisionPlan.setPrice(new BigDecimal("14.99"));
        AdditionalPackageEntity sportPackage = new AdditionalPackageEntity()
                .setName(AdditionalPackageEnum.SPORT_XTRA)
                .setPrice(new BigDecimal("5.00"));

        when(televisionPlanService.findEntityById(4L)).thenReturn(televisionPlan);
        when(additionalPackageService.findAllByIds(Set.of(7L))).thenReturn(Set.of(sportPackage));

        userService.signTelevisionPlan(4L, Set.of(7L), userDetails(), new byte[]{1});

        assertTrue(testUser.getUserTelevisionPlans().contains(televisionPlan));
        assertEquals(new BigDecimal("29.99"), testUser.getTotalDebtPerMonth());
        verify(contractService).addContract(eq(televisionPlan), eq(testUser), eq(Set.of(sportPackage)), any());
    }

    @Test
    void testUnsignPlan_ShouldDeductPricesAndDeactivateContract() {

        mockUserFound();
        testUser.setTotalDebtPerMonth(new BigDecimal("30.00"));

        ContractViewModel contract = new ContractViewModel();
        contract.setPlanId(5L);
        AdditionalPackageViewModel packageViewModel = new AdditionalPackageViewModel();
        packageViewModel.setPrice(new BigDecimal("5.00"));
        contract.setAdditionalPackageViewModels(Set.of(packageViewModel));
        when(contractService.findById(10L)).thenReturn(contract);

        PlanViewModel plan = new PlanViewModel();
        plan.setName("Fiber 300");
        plan.setPrice(new BigDecimal("20.00"));
        when(planService.findPlanById(5L)).thenReturn(plan);

        String planName = userService.unsignPlan(10L, "test_user", new byte[]{2});

        assertEquals("Fiber 300", planName);
        assertEquals(new BigDecimal("5.00"), testUser.getTotalDebtPerMonth());
        verify(contractService).deactivateContract(eq(10L), any());
    }

    @Test
    void testUnsignPlan_ShouldClampDebtAtZero() {

        mockUserFound();
        testUser.setTotalDebtPerMonth(new BigDecimal("10.00"));

        ContractViewModel contract = new ContractViewModel();
        contract.setPlanId(5L);
        when(contractService.findById(10L)).thenReturn(contract);

        PlanViewModel plan = new PlanViewModel();
        plan.setName("Fiber 300");
        plan.setPrice(new BigDecimal("20.00"));
        when(planService.findPlanById(5L)).thenReturn(plan);

        userService.unsignPlan(10L, "test_user", new byte[]{2});

        assertEquals(BigDecimal.ZERO, testUser.getTotalDebtPerMonth());
    }

    @Test
    void testGetAllVoicePlans_ShouldReturnEmptyList_WhenUserDoesNotExist() {

        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertTrue(userService.getAllVoicePlans("missing").isEmpty());
        assertTrue(userService.getAllDataPlans("missing").isEmpty());
        assertTrue(userService.getAllInternetPlans("missing").isEmpty());
        assertTrue(userService.getAllTelevisionPlans("missing").isEmpty());
    }

    @Test
    void testGetAllVoicePlans_ShouldMapActiveVoiceContracts() {

        VoicePlanEntity voicePlan = new VoicePlanEntity();
        voicePlan.setId(1L);
        bg.greencom.greencomwebapp.model.entity.ContractEntity contract =
                new bg.greencom.greencomwebapp.model.entity.ContractEntity()
                        .setPlan(voicePlan);
        contract.setId(11L);
        contract.setActive(true);
        testUser.getUserContracts().add(contract);
        mockUserFound();
        when(modelMapper.map(voicePlan, VoicePlanViewModel.class)).thenReturn(new VoicePlanViewModel());

        List<VoicePlanViewModel> result = userService.getAllVoicePlans("test_user");

        assertEquals(1, result.size());
        assertEquals(11L, result.get(0).getContractId());
    }

    @Test
    void testGetUserInfo_ShouldEnrichWithLoyaltyData() {

        mockUserFound();
        when(loyaltyFacade.getBalance("test_user"))
                .thenReturn(new LoyaltyResponse().setPointsBalance(150).setTier("SILVER"));

        UserViewModel result = userService.getUserInfo("test_user");

        assertEquals("test_user", result.getUsername());
        assertEquals(150, result.getLoyaltyPoints());
        assertEquals("SILVER", result.getLoyaltyTier());
    }

    @Test
    void testGetUserInfo_ShouldShowPlaceholder_WhenLoyaltyServiceIsDown() {

        mockUserFound();
        when(loyaltyFacade.getBalance("test_user")).thenReturn(null);

        UserViewModel result = userService.getUserInfo("test_user");

        assertEquals("—", result.getLoyaltyTier());
    }

    @Test
    void testRedeemLoyaltyPoints_ShouldApplyDiscountToDebt() {

        mockUserFound();
        testUser.setTotalDebtPerMonth(new BigDecimal("50.00"));
        when(loyaltyFacade.redeem("test_user", 200))
                .thenReturn(new LoyaltyResponse().setDiscountBgn(new BigDecimal("2.00")));

        BigDecimal discount = userService.redeemLoyaltyPoints("test_user", 200);

        assertEquals(new BigDecimal("2.00"), discount);
        assertEquals(new BigDecimal("48.00"), testUser.getTotalDebtPerMonth());
        verify(userRepository).saveAndFlush(testUser);
    }

    @Test
    void testRedeemLoyaltyPoints_ShouldThrow_WhenUserDoesNotExist() {

        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(LoyaltyException.class, () -> userService.redeemLoyaltyPoints("missing", 100));
    }

    @Test
    void testIsPenaltyRequired_ShouldReturnTrue_WhileContractIsActive() {

        ContractViewModel contract = new ContractViewModel();
        contract.setPlanId(5L);
        contract.setSignedOn(LocalDate.now().minusMonths(5));
        when(contractService.findById(10L)).thenReturn(contract);

        PlanViewModel plan = new PlanViewModel();
        plan.setPlanDuration("24");
        when(planService.findPlanById(5L)).thenReturn(plan);

        assertTrue(userService.isPenaltyRequired(10L));
    }

    @Test
    void testIsPenaltyRequired_ShouldReturnFalse_WhenContractHasExpired() {

        ContractViewModel contract = new ContractViewModel();
        contract.setPlanId(5L);
        contract.setSignedOn(LocalDate.now().minusMonths(25));
        when(contractService.findById(10L)).thenReturn(contract);

        PlanViewModel plan = new PlanViewModel();
        plan.setPlanDuration("24");
        when(planService.findPlanById(5L)).thenReturn(plan);

        assertFalse(userService.isPenaltyRequired(10L));
    }

    @Test
    void testCalculatePenalty_ShouldCapAtThreeMonths() {

        ContractViewModel contract = new ContractViewModel();
        contract.setPlanId(5L);
        contract.setSignedOn(LocalDate.now().minusMonths(5));
        when(contractService.findById(10L)).thenReturn(contract);

        PlanViewModel plan = new PlanViewModel();
        plan.setPlanDuration("24");
        plan.setPrice(new BigDecimal("20.00"));
        when(planService.findPlanById(5L)).thenReturn(plan);

        assertEquals(new BigDecimal("60.00"), userService.calculatePenalty(10L));
    }

    @Test
    void testCalculatePenalty_ShouldChargeRemainingMonths_NearContractEnd() {

        ContractViewModel contract = new ContractViewModel();
        contract.setPlanId(5L);
        contract.setSignedOn(LocalDate.now().minusMonths(23));
        when(contractService.findById(10L)).thenReturn(contract);

        PlanViewModel plan = new PlanViewModel();
        plan.setPlanDuration("24");
        plan.setPrice(new BigDecimal("20.00"));
        when(planService.findPlanById(5L)).thenReturn(plan);

        assertEquals(new BigDecimal("20.00"), userService.calculatePenalty(10L));
    }

    @Test
    void testGetAllUsers_ShouldMapUsersWithRoles() {

        testUser.getRoles().add(new UserRoleEntity().setName(UserRoleEnum.USER));
        when(userRepository.findAll()).thenReturn(List.of(testUser));
        when(modelMapper.map(testUser, UserViewModel.class)).thenReturn(new UserViewModel());

        List<UserViewModel> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getRoles().contains(UserRoleEnum.USER.getValue()));
    }

    @Test
    void testAddRole_ShouldAddRoleToExistingUser() {

        mockUserFound();
        when(userRoleService.findByName(UserRoleEnum.MODERATOR))
                .thenReturn(new UserRoleEntity().setName(UserRoleEnum.MODERATOR));

        assertTrue(userService.addRole("test_user", "MODERATOR"));
        assertEquals(1, testUser.getRoles().size());
        verify(userRepository).saveAndFlush(testUser);
    }

    @Test
    void testAddRole_ShouldReturnFalse_WhenUserDoesNotExist() {

        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());
        when(userRoleService.findByName(UserRoleEnum.MODERATOR)).thenReturn(new UserRoleEntity());

        assertFalse(userService.addRole("missing", "MODERATOR"));
    }

    @Test
    void testRemoveRole_ShouldRemoveRoleFromExistingUser() {

        UserRoleEntity moderatorRole = new UserRoleEntity().setName(UserRoleEnum.MODERATOR);
        testUser.getRoles().add(moderatorRole);
        mockUserFound();
        when(userRoleService.findByName(UserRoleEnum.MODERATOR)).thenReturn(moderatorRole);

        assertTrue(userService.removeRole("test_user", "MODERATOR"));
        assertTrue(testUser.getRoles().isEmpty());
    }

    @Test
    void testRemoveRole_ShouldReturnFalse_WhenUserDoesNotExist() {

        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());
        when(userRoleService.findByName(UserRoleEnum.MODERATOR)).thenReturn(new UserRoleEntity());

        assertFalse(userService.removeRole("missing", "MODERATOR"));
    }

    @Test
    void testEditUserProfile_ShouldUpdateFields() {

        mockUserFound();
        UserProfileEditBindingModel bindingModel = new UserProfileEditBindingModel();
        bindingModel.setFirstName("New");
        bindingModel.setLastName("Name");
        bindingModel.setEmail("new@example.com");

        assertTrue(userService.editUserProfile("test_user", bindingModel));
        assertEquals("New", testUser.getFirstName());
        assertEquals("new@example.com", testUser.getEmail());
        verify(userRepository).saveAndFlush(testUser);
    }

    @Test
    void testEditUserProfile_ShouldReturnFalse_WhenUserDoesNotExist() {

        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertFalse(userService.editUserProfile("missing", new UserProfileEditBindingModel()));
    }
}
