package bg.greencom.loyalty;

import bg.greencom.loyalty.model.LoyaltyAccount;
import bg.greencom.loyalty.repository.LoyaltyAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoyaltyIntegrationTest {

    private static final String USERNAME = "integration-user";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoyaltyAccountRepository loyaltyAccountRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        loyaltyAccountRepository.deleteAll();
        cacheManager.getCacheNames()
                .forEach(name -> Objects.requireNonNull(cacheManager.getCache(name)).clear());
    }

    @Test
    void fullLoyaltyFlow_earnRedeemRevoke_persistsState() throws Exception {
        mockMvc.perform(post("/api/loyalty/{username}/earn", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"points\":600}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pointsBalance").value(600))
                .andExpect(jsonPath("$.totalEarned").value(600));

        mockMvc.perform(get("/api/loyalty/{username}", USERNAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tier").value("SILVER"))
                .andExpect(jsonPath("$.pointsBalance").value(600));

        mockMvc.perform(put("/api/loyalty/{username}/redeem", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"points\":200}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pointsBalance").value(400))
                .andExpect(jsonPath("$.discountEur").value(2));

        mockMvc.perform(delete("/api/loyalty/{username}/points", USERNAME)
                        .param("amount", "500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pointsBalance").value(0));

        LoyaltyAccount account = loyaltyAccountRepository.findByUsername(USERNAME).orElseThrow();
        assertThat(account.getPointsBalance()).isZero();
        assertThat(account.getTotalEarned()).isEqualTo(600);
    }

    @Test
    void redeem_withInsufficientBalance_returnsBadRequestWithMessage() throws Exception {
        mockMvc.perform(post("/api/loyalty/{username}/earn", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"points\":100}"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/loyalty/{username}/redeem", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"points\":500}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").isNotEmpty());

        LoyaltyAccount account = loyaltyAccountRepository.findByUsername(USERNAME).orElseThrow();
        assertThat(account.getPointsBalance()).isEqualTo(100);
    }

    @Test
    void getAccount_cachesResponseUntilNextStateChange() throws Exception {
        mockMvc.perform(get("/api/loyalty/{username}", USERNAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pointsBalance").value(0));

        assertThat(Objects.requireNonNull(cacheManager.getCache("loyaltyAccounts")).get(USERNAME))
                .isNotNull();
    }
}
