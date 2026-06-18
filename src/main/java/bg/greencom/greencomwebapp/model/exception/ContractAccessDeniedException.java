package bg.greencom.greencomwebapp.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a logged-in user tries to access a contract they do not own.
 * The {@link ResponseStatus} makes Spring send a 403, which renders the custom
 * {@code templates/error/403.html} page.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ContractAccessDeniedException extends RuntimeException {

    public ContractAccessDeniedException(String message) {
        super(message);
    }
}
