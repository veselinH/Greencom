package bg.greencom.greencomwebapp.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.Map;

@Entity
@Table(name = "voice_mobile_plans")
public class VoicePlanEntity extends MobilePlanEntity {

    private String bgMinutes;
    private String roamingMinutes;

    public VoicePlanEntity() {
    }

    @Column(name = "bg_minutes", nullable = false)
    public String getBgMinutes() {
        return bgMinutes;
    }

    public VoicePlanEntity setBgMinutes(String bgMinutes) {
        this.bgMinutes = bgMinutes;
        return this;
    }

    @Column(name = "roaming_minutes")
    public String getRoamingMinutes() {
        return roamingMinutes;
    }

    public VoicePlanEntity setRoamingMinutes(String roamingMinutes) {
        this.roamingMinutes = roamingMinutes;
        return this;
    }

    @Override
    @Transient
    public String getPlanType() {
        return "Mobile Voice";
    }

    @Override
    @Transient
    public Map<String, String> getPlanDetails() {
        Map<String, String> details = super.getPlanDetails();
        details.put("BG Minutes", bgMinutes);
        if (roamingMinutes != null) {
            details.put("Roaming Minutes", roamingMinutes);
        }
        return details;
    }

}
