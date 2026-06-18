package bg.greencom.greencomwebapp.client.dto;

/**
 * Request body sent to the loyalty-service when redeeming points.
 * Mirrors {@code bg.greencom.loyalty.dto.RedeemRequest} in the microservice.
 */
public class RedeemRequest {

    private int points;

    public RedeemRequest() {
    }

    public RedeemRequest(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public RedeemRequest setPoints(int points) {
        this.points = points;
        return this;
    }
}
