package bg.greencom.greencomwebapp.client;

import bg.greencom.greencomwebapp.client.dto.EarnRequest;
import bg.greencom.greencomwebapp.client.dto.LoyaltyResponse;
import bg.greencom.greencomwebapp.client.dto.RedeemRequest;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoyaltyFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoyaltyFacade.class);

    private final LoyaltyClient loyaltyClient;

    public LoyaltyFacade(LoyaltyClient loyaltyClient) {
        this.loyaltyClient = loyaltyClient;
    }

    public LoyaltyResponse getBalance(String username) {
        try {
            return loyaltyClient.getBalance(username);
        } catch (Exception e) {
            LOGGER.warn("Could not fetch loyalty balance for '{}': {}", username, e.getMessage());
            return null;
        }
    }

    public void earn(String username, int points) {
        if (points <= 0) {
            return;
        }
        try {
            loyaltyClient.earn(username, new EarnRequest(points));
        } catch (Exception e) {
            LOGGER.warn("Could not award {} loyalty points to '{}': {}", points, username, e.getMessage());
        }
    }

    public void revoke(String username, int amount) {
        if (amount <= 0) {
            return;
        }
        try {
            loyaltyClient.revoke(username, amount);
        } catch (Exception e) {
            LOGGER.warn("Could not revoke {} loyalty points from '{}': {}", amount, username, e.getMessage());
        }
    }

    public LoyaltyResponse redeem(String username, int points) {
        try {
            return loyaltyClient.redeem(username, new RedeemRequest(points));
        } catch (FeignException.BadRequest e) {
            throw new LoyaltyException("You don't have enough points to redeem that amount.");
        } catch (Exception e) {
            LOGGER.warn("Could not redeem {} loyalty points for '{}': {}", points, username, e.getMessage());
            throw new LoyaltyException("The loyalty service is currently unavailable. Please try again later.");
        }
    }
}
