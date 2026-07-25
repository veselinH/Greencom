package bg.greencom.greencomwebapp.web;

import bg.greencom.greencomwebapp.model.user.GreencomUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private GreencomUserDetails principal() {
        return new GreencomUserDetails("ivan", "pass", "ivan@example.com",
                List.of(new SimpleGrantedAuthority("ROLE_USER")), "Ivanov");
    }

    @Test
    void index_returnsIndexView() throws Exception {
        mockMvc.perform(get("/").with(user(principal())))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void home_returnsHomeView() throws Exception {
        mockMvc.perform(get("/home").with(user(principal())))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    void about_returnsAboutView() throws Exception {
        mockMvc.perform(get("/about").with(user(principal())))
                .andExpect(status().isOk())
                .andExpect(view().name("about"));
    }
}
