package bg.greencom.greencomwebapp.model.binding;

import bg.greencom.greencomwebapp.validation.annotation.UniquePlanName;
import bg.greencom.greencomwebapp.validation.group.onCreate;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class TelevisionPlanBindingModel {

    private Long id;
    private String name;
    private String planDuration;
    private BigDecimal price;
    private String televisionType;
    private Integer channelCount;
    private Integer channelCountHD;

    public TelevisionPlanBindingModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @UniquePlanName(groups = onCreate.class)
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

    @NotNull(message = "Price is mandatory")
    @Positive(message = "Price must be positive number")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getTelevisionType() {
        return televisionType;
    }

    public void setTelevisionType(String televisionType) {
        this.televisionType = televisionType;
    }

    @NotNull(message = "Channel count is mandatory")
    @Positive(message = "Channel count must be positive")
    public Integer getChannelCount() {
        return channelCount;
    }

    public void setChannelCount(Integer channelCount) {
        this.channelCount = channelCount;
    }

    @Positive(message = "Channel count HD must be positive")
    public Integer getChannelCountHD() {
        return channelCountHD;
    }

    public void setChannelCountHD(Integer channelCountHD) {
        this.channelCountHD = channelCountHD;
    }
}
