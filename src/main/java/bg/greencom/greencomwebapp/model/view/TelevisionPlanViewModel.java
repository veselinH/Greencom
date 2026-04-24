package bg.greencom.greencomwebapp.model.view;

import bg.greencom.greencomwebapp.model.entity.AdditionalPackageEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TelevisionPlanViewModel {

    private Long id;
    private String name;
    private String planDuration;
    private BigDecimal price;
    private String televisionType;
    private Integer channelCount;
    private Integer channelCountHD;
    private Set<AdditionalPackageEntity> additionalPackageEntities = new HashSet<>();

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

    public String getTelevisionType() {
        return televisionType;
    }

    public void setTelevisionType(String televisionType) {
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

    public Set<AdditionalPackageEntity> getAdditionalPackageEntities() {
        return additionalPackageEntities;
    }

    public void setAdditionalPackageEntities(Set<AdditionalPackageEntity> additionalPackageEntities) {
        this.additionalPackageEntities = additionalPackageEntities;
    }
}
