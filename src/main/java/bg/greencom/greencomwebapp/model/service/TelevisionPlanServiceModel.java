package bg.greencom.greencomwebapp.model.service;

import bg.greencom.greencomwebapp.model.entity.enums.AdditionalPackageEnum;
import bg.greencom.greencomwebapp.model.entity.enums.TelevisionTypeEnum;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class TelevisionPlanServiceModel {

    private Long id;
    private String name;
    private String planDuration;
    private BigDecimal price;
    private TelevisionTypeEnum televisionType;
    private Integer channelCount;
    private Integer channelCountHD;
    private Set<AdditionalPackageEnum> additionalPackages = new HashSet<>();

    public TelevisionPlanServiceModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public TelevisionTypeEnum getTelevisionType() {
        return televisionType;
    }

    public void setTelevisionType(TelevisionTypeEnum televisionType) {
        this.televisionType = televisionType;
    }

    public Integer getChannelCount() {
        return channelCount;
    }

    public void setChannelCount(Integer channelCount) {
        this.channelCount = channelCount;
    }

    public Integer getChannelCountHD() {
        return channelCountHD;
    }

    public void setChannelCountHD(Integer channelCountHD) {
        this.channelCountHD = channelCountHD;
    }

    public Set<AdditionalPackageEnum> getAdditionalPackages() {
        return additionalPackages;
    }

    public void setAdditionalPackages(Set<AdditionalPackageEnum> additionalPackages) {
        this.additionalPackages = additionalPackages;
    }
}
