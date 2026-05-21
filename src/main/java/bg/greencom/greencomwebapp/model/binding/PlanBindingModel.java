package bg.greencom.greencomwebapp.model.binding;

import bg.greencom.greencomwebapp.validation.annotation.UniquePlanName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@UniquePlanName
public class PlanBindingModel {

    private Long id;
    private String name;
    private boolean isActive;

    public PlanBindingModel() {
    }

    public Long getId() {
        return id;
    }

    public PlanBindingModel setId(Long id) {
        this.id = id;
        return this;
    }

    @NotBlank(message = "Plan name is mandatory")
    @Size(min = 5, max = 9, message = "Size must be between 5 and 9")
    public String getName() {
        return name;
    }

    public PlanBindingModel setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
