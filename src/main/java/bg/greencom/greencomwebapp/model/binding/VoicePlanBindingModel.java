package bg.greencom.greencomwebapp.model.binding;

import bg.greencom.greencomwebapp.model.entity.enums.MobileExtraEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class VoicePlanBindingModel {
    private String name;
    private String planDuration;
    private String bgMinutes;
    private String roamingMinutes;
    private String bgInternetMegabytes;
    private String roamingInternetMegabytes;
    private BigDecimal price;
    private List<MobileExtraEnum> mobileExtras = new ArrayList<>();


    @NotBlank
    @Size(min = 5, max = 9, message = "Size must be between 5 and 9")
    public String getName() {
        return name;
    }

    public VoicePlanBindingModel setName(String name) {
        this.name = name;
        return this;
    }

    @NotBlank
    @Size(min = 5, max = 9, message = "Size must be between 5 and 9")
    public String getPlanDuration() {
        return planDuration;
    }

    public VoicePlanBindingModel setPlanDuration(String planDuration) {
        this.planDuration = planDuration;
        return this;
    }

    @NotBlank
    @Size(min = 3, max = 9, message = "Size must be between 3 and 9")
    public String getBgMinutes() {
        return bgMinutes;
    }

    public VoicePlanBindingModel setBgMinutes(String bgMinutes) {
        this.bgMinutes = bgMinutes;
        return this;
    }

    @NotBlank
    @Size(min = 1, max = 9, message = "Size must be between 1 and 9")
    public String getRoamingMinutes() {
        return roamingMinutes;
    }

    public VoicePlanBindingModel setRoamingMinutes(String roamingMinutes) {
        this.roamingMinutes = roamingMinutes;
        return this;
    }

    @NotBlank
    @Size(min = 1, max = 9, message = "Size must be between 1 and 9")
    public String getBgInternetMegabytes() {
        return bgInternetMegabytes;
    }

    public VoicePlanBindingModel setBgInternetMegabytes(String bgInternetMegabytes) {
        this.bgInternetMegabytes = bgInternetMegabytes;
        return this;
    }

    @NotBlank
    @Size(min = 1, max = 9, message = "Size must be between 1 and 9")
    public String getRoamingInternetMegabytes() {
        return roamingInternetMegabytes;
    }

    public VoicePlanBindingModel setRoamingInternetMegabytes(String roamingInternetMegabytes) {
        this.roamingInternetMegabytes = roamingInternetMegabytes;
        return this;
    }

    @NotNull
    @Positive(message = "Price must be positive number")
    public BigDecimal getPrice() {
        return price;
    }

    public VoicePlanBindingModel setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public List<MobileExtraEnum> getMobileExtras() {
        return mobileExtras;
    }

    public VoicePlanBindingModel setMobileExtras(List<MobileExtraEnum> mobileExtras) {
        this.mobileExtras = mobileExtras;
        return this;
    }
}
