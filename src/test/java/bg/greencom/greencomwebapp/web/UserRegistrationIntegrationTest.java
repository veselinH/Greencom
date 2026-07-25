package bg.greencom.greencomwebapp.web;

import bg.greencom.greencomwebapp.client.LoyaltyFacade;
import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.repository.UserRepository;
import bg.greencom.greencomwebapp.util.DBInit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserRegistrationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DBInit dbInit;

    @MockitoBean
    private LoyaltyFacade loyaltyFacade;

    @BeforeEach
    void setUp() throws Exception {
        dbInit.run();
    }

    @Test
    void register_persistsUserAndRedirectsHome() throws Exception {
        mockMvc.perform(post("/users/register")
                        .with(csrf())
                        .param("firstName", "Maria")
                        .param("lastName", "Petrova")
                        .param("username", "maria")
                        .param("email", "maria@example.com")
                        .param("password", "password1")
                        .param("confirmPassword", "password1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        UserEntity saved = userRepository.findByUsername("maria").orElseThrow();
        assertThat(saved.getEmail()).isEqualTo("maria@example.com");
        assertThat(saved.getPassword()).isNotEqualTo("password1");
        assertThat(saved.getRoles()).isNotEmpty();
    }

    @Test
    void register_rejectsDuplicateUsername() throws Exception {
        long usersBefore = userRepository.count();

        mockMvc.perform(post("/users/register")
                        .with(csrf())
                        .param("firstName", "Fake")
                        .param("lastName", "Admin")
                        .param("username", "admin")
                        .param("email", "fake-admin@example.com")
                        .param("password", "password1")
                        .param("confirmPassword", "password1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("register"));

        assertThat(userRepository.count()).isEqualTo(usersBefore);
    }

    @Test
    void profile_requiresAuthentication() throws Exception {
        mockMvc.perform(get("/users/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/users/login"));
    }

    @Test
    void home_pageIsOpenForAnonymousUsers() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }
}
