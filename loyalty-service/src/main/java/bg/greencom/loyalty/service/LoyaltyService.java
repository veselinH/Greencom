package bg.greencom.loyalty.service;

import bg.greencom.loyalty.dto.LoyaltyResponse;

public interface LoyaltyService {

    LoyaltyResponse getAccount(String username);

    LoyaltyResponse earn(String username, int points);

    LoyaltyResponse redeem(String username, int points);

    LoyaltyResponse revoke(String username, int amount);

    int awardTierBonusToAll();
}
