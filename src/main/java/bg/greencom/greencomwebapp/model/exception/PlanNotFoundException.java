package bg.greencom.greencomwebapp.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Plan was not found.")
public class PlanNotFoundException extends RuntimeException{

    private final String planName;

    public PlanNotFoundException(String planName) {
        super("Plan with ID " + planName + " not found!");
        this.planName = planName;
    }

    public String getPlanId() {
        return planName;
    }
}
