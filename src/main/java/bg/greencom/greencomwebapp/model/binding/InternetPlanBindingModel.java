package bg.greencom.greencomwebapp.model.binding;

import bg.greencom.greencomwebapp.model.entity.enums.InternetExtraEnum;
import bg.greencom.greencomwebapp.model.entity.enums.InternetTypeEnum;
import bg.greencom.greencomwebapp.validation.annotation.UniquePlanName;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InternetPlanBindingModel {

    private Long id;
    private String name;
    private String planDuration;
    private Integer downloadMbps;
    private Integer uploadMbps;
    private BigDecimal price;
    private InternetTypeEnum internetType;
    private List<InternetExtraEnum> internetExtras = new ArrayList<>();

    public InternetPlanBindingModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @UniquePlanName
    @NotBlank(message = "Plan name is mandatory")
    @Size(min = 5, max = 9, message = "Size must be between 5 and 9")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotBlank(message = "Plan duration is mandatory")
    @Size(min = 2, max = 9, message = "Size must be between 2 and 9")
    public String getPlanDuration() {
        return planDuration;
    }

    public void setPlanDuration(String planDuration) {
        this.planDuration = planDuration;
    }

    @NotNull(message = "Download speed mandatory")
    @Min(value = 10, message = "Minimum value of 10 required")
    @Max(value = 1000, message = "Maximum value is 1000")
    public Integer getDownloadMbps() {
        return downloadMbps;
    }

    public void setDownloadMbps(Integer downloadMbps) {
        this.downloadMbps = downloadMbps;
    }

    @NotNull(message = "Upload speed mandatory")
    @Min(value = 10, message = "Minimum value of 10 required")
    @Max(value = 1000, message = "Maximum value is 1000")
    public Integer getUploadMbps() {
        return uploadMbps;
    }

    public void setUploadMbps(Integer uploadMbps) {
        this.uploadMbps = uploadMbps;
    }

    @NotNull(message = "Price is mandatory")
    @Positive(message = "Price must be positive number")
    public BigDecimal getPrice() {
        return price;
    }

    public InternetPlanBindingModel setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public InternetTypeEnum getInternetType() {
        return internetType;
    }

    public InternetPlanBindingModel setInternetType(InternetTypeEnum internetType) {
        this.internetType = internetType;
        return this;
    }

    public List<InternetExtraEnum> getInternetExtras() {
        return internetExtras;
    }

    public InternetPlanBindingModel setInternetExtras(List<InternetExtraEnum> internetExtras) {
        this.internetExtras = internetExtras;
        return this;
    }
}
