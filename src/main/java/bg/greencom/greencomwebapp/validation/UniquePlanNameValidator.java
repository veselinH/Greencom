package bg.greencom.greencomwebapp.validation;

import bg.greencom.greencomwebapp.model.binding.PlanBindingModel;
import bg.greencom.greencomwebapp.model.binding.VoicePlanBindingModel;
import bg.greencom.greencomwebapp.model.entity.PlanEntity;
import bg.greencom.greencomwebapp.service.PlanService;
import bg.greencom.greencomwebapp.validation.annotation.UniquePlanName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class UniquePlanNameValidator implements ConstraintValidator<UniquePlanName, PlanBindingModel> {
    private final PlanService planService;

    public UniquePlanNameValidator(PlanService planService) {
        this.planService = planService;
    }

    @Override
    public boolean isValid(PlanBindingModel bindingModel, ConstraintValidatorContext context) {

        boolean isValid;

        if (!bindingModel.isActive() && bindingModel.getId() != null){
            return true;
        }

        PlanEntity activePlan = this.planService.findActivePlanByName(bindingModel.getName());

        if (activePlan == null){
            return true;
        }

        isValid = activePlan.getId().equals(bindingModel.getId());

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("name")
                    .addConstraintViolation();
        }

        return isValid;
    }
}
