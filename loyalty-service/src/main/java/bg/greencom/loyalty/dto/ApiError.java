package bg.greencom.loyalty.dto;

import java.time.LocalDateTime;

public record ApiError(int status, String error, String message, LocalDateTime timestamp) {

    public static ApiError badRequest(String message) {
        return new ApiError(400, "Bad Request", message, LocalDateTime.now());
    }
}
