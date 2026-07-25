package bg.greencom.loyalty.service;

public class InsufficientPointsException extends RuntimeException {

    public InsufficientPointsException(String message) {
        super(message);
    }
}
