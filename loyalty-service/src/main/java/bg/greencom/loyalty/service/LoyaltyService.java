package bg.greencom.loyalty.service;

import bg.greencom.loyalty.dto.LoyaltyResponse;

public interface LoyaltyService {

    /**
     * Returns the account for the given username, creating a fresh
     * zero-balance account on first access so callers always get a result.
     */
    LoyaltyResponse getAccount(String username);

    /**
     * Adds points to the account (creating it if needed). Triggered when a
     * user signs a contract in the main application.
     */
    LoyaltyResponse earn(String username, int points);

    /**
     * Deducts points from the account. Throws {@link InsufficientPointsException}
     * if the balance is not high enough.
     */
    LoyaltyResponse redeem(String username, int points);

    /**
     * Removes up to {@code amount} points from the account (floored at zero).
     * Triggered when a user unsigns a contract in the main application.
     */
    LoyaltyResponse revoke(String username, int amount);
}
