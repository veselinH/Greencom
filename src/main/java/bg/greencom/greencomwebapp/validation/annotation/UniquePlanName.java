package bg.greencom.greencomwebapp.validation.annotation;

import bg.greencom.greencomwebapp.validation.UniquePlanNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniquePlanNameValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniquePlanName {
    String message() default "Active plan with the same name already exists";
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}