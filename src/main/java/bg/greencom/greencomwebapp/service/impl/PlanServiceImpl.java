package bg.greencom.greencomwebapp.service.impl;

import bg.greencom.greencomwebapp.model.entity.PlanEntity;
import bg.greencom.greencomwebapp.model.entity.UserEntity;
import bg.greencom.greencomwebapp.service.PlanService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
@Component
public class PlanServiceImpl implements PlanService {

    @Override
    public void removePlanAndAdjustDebt(List<UserEntity> users, PlanEntity plan) {
        for (UserEntity userEntity : users) {
            // Count occurrences of this specific plan
            long count = userEntity.getUserVoiceMobilePlans().stream()
                    .filter(p -> p.equals(plan))
                    .count();

            if (count > 0) {
                // Calculate the total reduction (Price * Count)
                BigDecimal totalReduction = plan.getPrice()
                        .multiply(BigDecimal.valueOf(count));

                // Subtract from user debt
                userEntity.setTotalDebtPerMonth(
                        userEntity.getTotalDebtPerMonth().subtract(totalReduction)
                );

                // Cleanly remove all instances from the list
                userEntity.getUserVoiceMobilePlans().removeIf(p -> p.equals(plan));
            }
        }
    }
}
