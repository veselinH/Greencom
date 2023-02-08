package bg.greencom.greencomwebapp.model.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "television_plans")
public class TelevisionPlanEntity extends PlanEntity {

    private Integer channelCount;
    private Integer channelCountInHD;
    private BigDecimal price;
    private Set<AdditionalPackageEntity> additionalPackages;
    private TelevisionTypeEntity televisionType;

    public TelevisionPlanEntity() {
    }

    @Column(name = "channel_count", nullable = false)
    public Integer getChannelCount() {
        return channelCount;
    }

    public TelevisionPlanEntity setChannelCount(Integer channelCount) {
        this.channelCount = channelCount;
        return this;
    }

    @Column(name = "channel_count_in_hd")
    public Integer getChannelCountInHD() {
        return channelCountInHD;
    }

    public TelevisionPlanEntity setChannelCountInHD(Integer channelCountInHD) {
        this.channelCountInHD = channelCountInHD;
        return this;
    }

    @Column(nullable = false)
    public BigDecimal getPrice() {
        return price;
    }

    public TelevisionPlanEntity setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    @OneToMany
    public Set<AdditionalPackageEntity> getAdditionalPackages() {
        return additionalPackages;
    }

    public TelevisionPlanEntity setAdditionalPackages(Set<AdditionalPackageEntity> additionalPackages) {
        this.additionalPackages = additionalPackages;
        return this;
    }

    @ManyToOne
    public TelevisionTypeEntity getTelevisionType() {
        return televisionType;
    }

    public TelevisionPlanEntity setTelevisionType(TelevisionTypeEntity televisionType) {
        this.televisionType = televisionType;
        return this;
    }
}
