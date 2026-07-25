package bg.greencom.greencomwebapp.web;

import bg.greencom.greencomwebapp.client.LoyaltyException;
import bg.greencom.greencomwebapp.model.binding.UserRegisterBindingModel;
import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.model.service.UserServiceModel;
import bg.greencom.greencomwebapp.model.user.GreencomUserDetails;
import bg.greencom.greencomwebapp.service.ContractService;
import bg.greencom.greencomwebapp.service.impl.UserServiceImpl;
import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    private static final String SIGNATURE = "data:image/png;base64,QUJD";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserServiceImpl userService;

    @MockitoBean
    private ModelMapper modelMapper;

    @MockitoBean
    private SecurityContextRepository securityContextRepository;

    @MockitoBean
    private ContractService contractService;

    private GreencomUserDetails principal() {
        return new GreencomUserDetails("ivan", "pass", "ivan@example.com",
                List.of(new SimpleGrantedAuthority("ROLE_USER")), "Ivanov");
    }

    private GreencomUserDetails adminPrincipal() {
        return new GreencomUserDetails("admin", "pass", "admin@example.com",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")), "Adminov");
    }

    @Test
    void login_redirectsHome_whenAlreadyAuthenticated() throws Exception {
        mockMvc.perform(get("/users/login").with(user(principal())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    void onFailedLogin_flashesBadCredentials() throws Exception {
        mockMvc.perform(post("/users/login-errors")
                        .with(user(principal()))
                        .with(csrf())
                        .param("username", "ivan"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/login"))
                .andExpect(flash().attribute("bad_credentials", true));
    }

    @Test
    void register_redirectsHome_whenAlreadyAuthenticated() throws Exception {
        mockMvc.perform(get("/users/register").with(user(principal())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    void registerUser_registersAndRedirectsHome() throws Exception {
        when(modelMapper.map(any(UserRegisterBindingModel.class), eq(UserServiceModel.class)))
                .thenReturn(new UserServiceModel().setUsername("ivan"));

        mockMvc.perform(post("/users/register")
                        .with(user(principal()))
                        .with(csrf())
                        .param("firstName", "Ivan")
                        .param("lastName", "Ivanov")
                        .param("username", "ivan")
                        .param("email", "ivan@example.com")
                        .param("password", "password1")
                        .param("confirmPassword", "password1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(userService).registerUser(any(UserServiceModel.class), any());
    }

    @Test
    void registerUser_redirectsBack_whenPasswordsDiffer() throws Exception {
        mockMvc.perform(post("/users/register")
                        .with(user(principal()))
                        .with(csrf())
                        .param("firstName", "Ivan")
                        .param("lastName", "Ivanov")
                        .param("username", "ivan")
                        .param("email", "ivan@example.com")
                        .param("password", "password1")
                        .param("confirmPassword", "different"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("register"))
                .andExpect(flash().attributeExists("userRegisterBindingModel"));
    }

    @Test
    void unsignPlan_flashesPenalty_whenPenaltyIsRequired() throws Exception {
        when(userService.isPenaltyRequired(10L)).thenReturn(true);
        when(userService.calculatePenalty(10L)).thenReturn(new BigDecimal("60.00"));

        mockMvc.perform(patch("/users/unsign/plan/{id}", 10L)
                        .with(user(principal()))
                        .with(csrf())
                        .param("signature", SIGNATURE))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/profile"))
                .andExpect(flash().attribute("showPenalty", true))
                .andExpect(flash().attribute("penaltyAmount", new BigDecimal("60.00")));
    }

    @Test
    void unsignPlan_unsignsImmediately_whenNoPenaltyIsRequired() throws Exception {
        when(userService.isPenaltyRequired(10L)).thenReturn(false);
        when(userService.unsignPlan(eq(10L), eq("ivan"), any())).thenReturn("Fiber 300");

        mockMvc.perform(patch("/users/unsign/plan/{id}", 10L)
                        .with(user(principal()))
                        .with(csrf())
                        .param("signature", SIGNATURE))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("successMessage", "Plan 'Fiber 300' successfully unsigned."));
    }

    @Test
    void confirmUnsign_rejectsMissingPaymentDetails() throws Exception {
        mockMvc.perform(patch("/users/confirm-unsign")
                        .with(user(principal()))
                        .with(csrf())
                        .param("contractId", "10")
                        .param("signature", SIGNATURE))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("error", "Payment details are required."));
    }

    @Test
    void confirmUnsign_rejectsInvalidPaymentDetails() throws Exception {
        mockMvc.perform(patch("/users/confirm-unsign")
                        .with(user(principal()))
                        .with(csrf())
                        .param("contractId", "10")
                        .param("signature", SIGNATURE)
                        .param("cardNumber", "1234")
                        .param("cardCVC", "12"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("error", "Invalid payment details."));
    }

    @Test
    void confirmUnsign_unsignsWithValidPaymentDetails() throws Exception {
        when(userService.unsignPlan(eq(10L), eq("ivan"), any())).thenReturn("TV Max");

        mockMvc.perform(patch("/users/confirm-unsign")
                        .with(user(principal()))
                        .with(csrf())
                        .param("contractId", "10")
                        .param("signature", SIGNATURE)
                        .param("cardNumber", "1234 5678 9012 3456")
                        .param("cardCVC", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("successMessage", "Plan 'TV Max' successfully unsigned."));
    }

    @Test
    void redeemLoyaltyPoints_flashesSuccess() throws Exception {
        when(userService.redeemLoyaltyPoints("ivan", 200)).thenReturn(new BigDecimal("2.00"));

        mockMvc.perform(post("/users/loyalty/redeem")
                        .with(user(principal()))
                        .with(csrf())
                        .param("points", "200"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void redeemLoyaltyPoints_rejectsNonPositivePoints() throws Exception {
        mockMvc.perform(post("/users/loyalty/redeem")
                        .with(user(principal()))
                        .with(csrf())
                        .param("points", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void redeemLoyaltyPoints_flashesErrorFromLoyaltyService() throws Exception {
        when(userService.redeemLoyaltyPoints("ivan", 500))
                .thenThrow(new LoyaltyException("You don't have enough points to redeem that amount."));

        mockMvc.perform(post("/users/loyalty/redeem")
                        .with(user(principal()))
                        .with(csrf())
                        .param("points", "500"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("error", "You don't have enough points to redeem that amount."));
    }

    @Test
    void downloadContract_returnsPdfForOwner() throws Exception {
        when(contractService.isContractOwner(10L, "ivan")).thenReturn(true);
        when(contractService.generateContractPdf(10L)).thenReturn(new byte[]{1, 2, 3});
        when(contractService.getContractDownloadFileName(10L)).thenReturn("contract.pdf");

        mockMvc.perform(get("/users/contract/{id}/download", 10L).with(user(principal())))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"));
    }

    @Test
    void downloadContract_returns403ForNonOwner() throws Exception {
        when(contractService.isContractOwner(10L, "ivan")).thenReturn(false);

        mockMvc.perform(get("/users/contract/{id}/download", 10L).with(user(principal())))
                .andExpect(status().isForbidden())
                .andExpect(view().name("error/403"));
    }

    @Test
    void downloadContract_returns404WhenContractDoesNotExist() throws Exception {
        when(contractService.isContractOwner(10L, "admin")).thenReturn(false);
        when(contractService.generateContractPdf(10L))
                .thenThrow(new ObjectNotFoundException((Object) 10L, "contract"));

        mockMvc.perform(get("/users/contract/{id}/download", 10L).with(user(adminPrincipal())))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"));
    }

    @Test
    void addRole_flashesSuccess() throws Exception {
        when(userService.addRole("ivan", "MODERATOR")).thenReturn(true);

        mockMvc.perform(post("/users/roles/add")
                        .with(user(adminPrincipal()))
                        .with(csrf())
                        .param("username", "ivan")
                        .param("role", "MODERATOR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/roles"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void addRole_flashesError_whenUserDoesNotExist() throws Exception {
        when(userService.addRole("missing", "MODERATOR")).thenReturn(false);

        mockMvc.perform(post("/users/roles/add")
                        .with(user(adminPrincipal()))
                        .with(csrf())
                        .param("username", "missing")
                        .param("role", "MODERATOR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void removeRole_flashesSuccess() throws Exception {
        when(userService.removeRole("ivan", "MODERATOR")).thenReturn(true);

        mockMvc.perform(post("/users/roles/remove")
                        .with(user(adminPrincipal()))
                        .with(csrf())
                        .param("username", "ivan")
                        .param("role", "MODERATOR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void editProfile_updatesProfile() throws Exception {
        when(userService.findUserByEmail("new@example.com")).thenReturn(null);
        when(userService.editUserProfile(eq("ivan"), any())).thenReturn(true);

        mockMvc.perform(post("/users/profile/edit")
                        .with(user(principal()))
                        .with(csrf())
                        .param("firstName", "Ivan")
                        .param("lastName", "Ivanov")
                        .param("email", "new@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/profile"))
                .andExpect(flash().attribute("successMessage", "Profile successfully edited."));
    }

    @Test
    void editProfile_rejectsEmailTakenByAnotherUser() throws Exception {
        UserEntity otherUser = new UserEntity();
        otherUser.setUsername("other");
        when(userService.findUserByEmail("taken@example.com")).thenReturn(otherUser);

        mockMvc.perform(post("/users/profile/edit")
                        .with(user(principal()))
                        .with(csrf())
                        .param("firstName", "Ivan")
                        .param("lastName", "Ivanov")
                        .param("email", "taken@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("openEditModal", true));
    }
}
