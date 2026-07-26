package bg.greencom.greencomwebapp.client.dto;

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
