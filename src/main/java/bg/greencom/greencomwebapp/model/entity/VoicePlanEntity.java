package bg.greencom.greencomwebapp.model.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

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
}
