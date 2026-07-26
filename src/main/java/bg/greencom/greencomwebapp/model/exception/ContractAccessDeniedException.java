package bg.greencom.greencomwebapp.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ContractAccessDeniedException extends RuntimeException {

    public ContractAccessDeniedException(String message) {
        super(message);
    }
}
