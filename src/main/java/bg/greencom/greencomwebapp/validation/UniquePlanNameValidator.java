package bg.greencom.greencomwebapp.validation;

import bg.greencom.greencomwebapp.service.PlanService;
import bg.greencom.greencomwebapp.validation.annotation.UniquePlanName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class UniquePlanNameValidator implements ConstraintValidator<UniquePlanName, String> {
    private final PlanService planService;

    public UniquePlanNameValidator(PlanService planService) {
        this.planService = planService;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return this.planService.findPlanByName(value) == null;
    }
}
