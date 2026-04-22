package bg.greencom.greencomwebapp.model.view;

import java.math.BigDecimal;

public class PlanViewModel {
    private String name;
    private String planDuration;
    private BigDecimal price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlanDuration() {
        return planDuration;
    }

    public void setPlanDuration(String planDuration) {
        this.planDuration = planDuration;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
