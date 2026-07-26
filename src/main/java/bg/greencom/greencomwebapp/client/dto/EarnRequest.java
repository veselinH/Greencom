package bg.greencom.greencomwebapp.client.dto;

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
