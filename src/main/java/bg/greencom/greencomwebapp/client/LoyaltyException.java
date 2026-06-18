package bg.greencom.greencomwebapp.client;

/**
 * Raised when a loyalty redemption cannot be completed (insufficient points or
 * the loyalty-service being unreachable). Carries a user-facing message so the
 * web layer can surface it as a flash error.
 */
public class LoyaltyException extends RuntimeException {

    public LoyaltyException(String message) {
        super(message);
    }
}
