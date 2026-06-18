package bg.greencom.loyalty.web;

import bg.greencom.loyalty.dto.LoyaltyResponse;
import bg.greencom.loyalty.dto.RedeemRequest;
import bg.greencom.loyalty.service.LoyaltyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoyaltyController.class)
class LoyaltyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LoyaltyService loyaltyService;

    private LoyaltyResponse response(int balance, int totalEarned, String tier, BigDecimal discount) {
        return new LoyaltyResponse()
                .setUsername("ivan")
                .setPointsBalance(balance)
                .setTotalEarned(totalEarned)
                .setTier(tier)
                .setDiscountBgn(discount);
    }

    @Test
    void getAccount_returnsBalanceAndTier() throws Exception {
        when(loyaltyService.getAccount("ivan"))
                .thenReturn(response(250, 250, "BRONZE", BigDecimal.ZERO));

        mockMvc.perform(get("/api/loyalty/{username}", "ivan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("ivan"))
                .andExpect(jsonPath("$.pointsBalance").value(250))
                .andExpect(jsonPath("$.tier").value("BRONZE"));
    }

    @Test
    void earn_addsPoints() throws Exception {
        when(loyaltyService.earn(eq("ivan"), eq(100)))
                .thenReturn(response(100, 100, "BRONZE", BigDecimal.ZERO));

        mockMvc.perform(post("/api/loyalty/{username}/earn", "ivan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"points\":100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pointsBalance").value(100));
    }

    @Test
    void earn_rejectsNonPositivePoints() throws Exception {
        mockMvc.perform(post("/api/loyalty/{username}/earn", "ivan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"points\":0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void redeem_returnsDiscount() throws Exception {
        when(loyaltyService.redeem(eq("ivan"), eq(200)))
                .thenReturn(response(50, 250, "BRONZE", new BigDecimal("2.00")));

        mockMvc.perform(put("/api/loyalty/{username}/redeem", "ivan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RedeemRequest().setPoints(200))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pointsBalance").value(50))
                .andExpect(jsonPath("$.discountBgn").value(2.00));
    }
}
