package bg.greencom.greencomwebapp.client;

import bg.greencom.greencomwebapp.client.dto.EarnRequest;
import bg.greencom.greencomwebapp.client.dto.LoyaltyResponse;
import bg.greencom.greencomwebapp.client.dto.RedeemRequest;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Thin wrapper around {@link LoyaltyClient} that keeps the loyalty-service a
 * non-critical dependency: read/award/revoke calls degrade gracefully (log and
 * carry on) so the loyalty-service being down never breaks signing, unsigning or
 * the profile page. Only {@link #redeem} surfaces failures, since the user
 * explicitly requested that action and needs feedback.
 */
@Component
public class LoyaltyFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoyaltyFacade.class);

    private final LoyaltyClient loyaltyClient;

    public LoyaltyFacade(LoyaltyClient loyaltyClient) {
        this.loyaltyClient = loyaltyClient;
    }

    /**
     * Returns the account for the profile page, or {@code null} if the
     * loyalty-service cannot be reached (the page then shows a placeholder).
     */
    public LoyaltyResponse getBalance(String username) {
        try {
            return loyaltyClient.getBalance(username);
        } catch (Exception e) {
            LOGGER.warn("Could not fetch loyalty balance for '{}': {}", username, e.getMessage());
            return null;
        }
    }

    /** Awards points when a contract is signed. Best-effort. */
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

    /** Removes points when a contract is unsigned. Best-effort. */
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

    /**
     * Redeems points for a discount. Throws {@link LoyaltyException} with a
     * user-facing message if the balance is insufficient or the service is down.
     */
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
