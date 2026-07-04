package bg.greencom.greencomwebapp.model.entity;

import jakarta.persistence.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "television_plans")
public class TelevisionPlanEntity extends PlanEntity {

    private Integer channelCount;
    private Integer channelCountInHD;
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

    @ManyToOne
    public TelevisionTypeEntity getTelevisionType() {
        return televisionType;
    }

    public TelevisionPlanEntity setTelevisionType(TelevisionTypeEntity televisionType) {
        this.televisionType = televisionType;
        return this;
    }

    @Override
    @Transient
    public String getPlanType() {
        return "Television";
    }

    @Override
    @Transient
    public Map<String, String> getPlanDetails() {
        Map<String, String> details = new LinkedHashMap<>();
        details.put("Channels", String.valueOf(channelCount));
        if (channelCountInHD != null) {
            details.put("HD Channels", String.valueOf(channelCountInHD));
        }
        if (televisionType != null && televisionType.getName() != null) {
            details.put("Television Type", televisionType.getName().name());
        }
        return details;
    }
}
