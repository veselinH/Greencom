package bg.greencom.greencomwebapp.client.dto;

import java.math.BigDecimal;

public class LoyaltyResponse {

    private String username;
    private int pointsBalance;
    private int totalEarned;
    private String tier;
    private BigDecimal discountEur;

    public LoyaltyResponse() {
    }

    public String getUsername() {
        return username;
    }

    public LoyaltyResponse setUsername(String username) {
        this.username = username;
        return this;
    }

    public int getPointsBalance() {
        return pointsBalance;
    }

    public LoyaltyResponse setPointsBalance(int pointsBalance) {
        this.pointsBalance = pointsBalance;
        return this;
    }

    public int getTotalEarned() {
        return totalEarned;
    }

    public LoyaltyResponse setTotalEarned(int totalEarned) {
        this.totalEarned = totalEarned;
        return this;
    }

    public String getTier() {
        return tier;
    }

    public LoyaltyResponse setTier(String tier) {
        this.tier = tier;
        return this;
    }

    public BigDecimal getDiscountEur() {
        return discountEur;
    }

    public LoyaltyResponse setDiscountEur(BigDecimal discountEur) {
        this.discountEur = discountEur;
        return this;
    }
}
