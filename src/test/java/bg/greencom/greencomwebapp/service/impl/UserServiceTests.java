package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.model.entity.UserRoleEntity;
import bg.greencom.greencomwebapp.model.entity.enums.UserRoleEnum;
import bg.greencom.greencomwebapp.repository.UserRepository;
import bg.greencom.greencomwebapp.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setUsername("test_user");
        testUser.setEmail("test@example.com");
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
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_pass");

        userService.initialize();

        verify(userRepository, times(1)).save(any(UserEntity.class));
    }
}
