package bg.greencom.loyalty.dto;

import jakarta.validation.constraints.Positive;

public class RedeemRequest {

    @Positive(message = "Points to redeem must be positive.")
    private int points;

    public RedeemRequest() {
    }

    public int getPoints() {
        return points;
    }

    public RedeemRequest setPoints(int points) {
        this.points = points;
        return this;
    }
}
