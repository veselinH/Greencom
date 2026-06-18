package bg.greencom.greencomwebapp.client.dto;

/**
 * Request body sent to the loyalty-service when awarding points.
 * Mirrors {@code bg.greencom.loyalty.dto.EarnRequest} in the microservice.
 */
public class EarnRequest {

    private int points;

    public EarnRequest() {
    }

    public EarnRequest(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public EarnRequest setPoints(int points) {
        this.points = points;
        return this;
    }
}
