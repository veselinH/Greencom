package bg.greencom.greencomwebapp.model.binding;

import bg.greencom.greencomwebapp.model.entity.enums.MobileExtraEnum;
import bg.greencom.greencomwebapp.validation.annotation.UniquePlanName;
import bg.greencom.greencomwebapp.validation.group.onCreate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DataPlanBindingModel {

    private Long id;
    private String name;
    private String planDuration;
    private String bgInternetMegabytes;
    private String roamingInternetMegabytes;
    private BigDecimal price;
    private List<MobileExtraEnum> mobileExtras = new ArrayList<>();


    public DataPlanBindingModel() {
    }

    public Long getId() {
        return id;
    }

    public DataPlanBindingModel setId(Long id) {
        this.id = id;
        return this;
    }

    @UniquePlanName(groups = onCreate.class)
    @NotBlank(message = "Plan name is mandatory")
    @Size(min = 5, max = 9, message = "Size must be between 5 and 9")
    public String getName() {
        return name;
    }

    public DataPlanBindingModel setName(String name) {
        this.name = name;
        return this;
    }

    @NotBlank(message = "Plan duration is mandatory")
    @Size(min = 2, max = 9, message = "Size must be between 2 and 9")
    public String getPlanDuration() {
        return planDuration;
    }

    public DataPlanBindingModel setPlanDuration(String planDuration) {
        this.planDuration = planDuration;
        return this;
    }

    @NotBlank(message = "Megabytes are mandatory")
    @Size(min = 1, max = 9, message = "Size must be between 1 and 9")
    public String getBgInternetMegabytes() {
        return bgInternetMegabytes;
    }

    public DataPlanBindingModel setBgInternetMegabytes(String bgInternetMegabytes) {
        this.bgInternetMegabytes = bgInternetMegabytes;
        return this;
    }

    @NotBlank(message = "Roaming megabytes are mandatory")
    @Size(min = 1, max = 9, message = "Size must be between 1 and 9")
    public String getRoamingInternetMegabytes() {
        return roamingInternetMegabytes;
    }

    public DataPlanBindingModel setRoamingInternetMegabytes(String roamingInternetMegabytes) {
        this.roamingInternetMegabytes = roamingInternetMegabytes;
        return this;
    }

    @NotNull(message = "Price is mandatory")
    @Positive(message = "Price must be positive number")
    public BigDecimal getPrice() {
        return price;
    }

    public DataPlanBindingModel setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public List<MobileExtraEnum> getMobileExtras() {
        return mobileExtras;
    }

    public DataPlanBindingModel setMobileExtras(List<MobileExtraEnum> mobileExtras) {
        this.mobileExtras = mobileExtras;
        return this;
    }
}
