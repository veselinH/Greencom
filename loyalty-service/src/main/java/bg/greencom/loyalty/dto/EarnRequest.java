package bg.greencom.loyalty.dto;

import jakarta.validation.constraints.Positive;

public class EarnRequest {

    @Positive(message = "Points to earn must be positive.")
    private int points;

    public EarnRequest() {
    }

    public int getPoints() {
        return points;
    }

    public EarnRequest setPoints(int points) {
        this.points = points;
        return this;
    }
}
