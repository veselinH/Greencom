package bg.greencom.loyalty.web;

import bg.greencom.loyalty.dto.ApiError;
import bg.greencom.loyalty.service.InsufficientPointsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(InsufficientPointsException.class)
    public ResponseEntity<ApiError> handleInsufficientPoints(InsufficientPointsException exception) {
        LOGGER.warn("Redeem rejected: {}", exception.getMessage());

        return ResponseEntity
                .badRequest()
                .body(ApiError.badRequest(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        LOGGER.warn("Request validation failed: {}", message);

        return ResponseEntity
                .badRequest()
                .body(ApiError.badRequest(message));
    }
}
