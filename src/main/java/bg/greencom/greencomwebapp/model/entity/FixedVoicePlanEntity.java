package bg.greencom.greencomwebapp.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "fixed_voice_plans")
public class FixedVoicePlanEntity extends PlanEntity{

    private String minutesInBg;
    private String internationalMinutes;

    public FixedVoicePlanEntity() {
    }

    @Column(name = "minutes_in_bg", nullable = false)
    public String getMinutesInBg() {
        return minutesInBg;
    }

    public FixedVoicePlanEntity setMinutesInBg(String minutesInBg) {
        this.minutesInBg = minutesInBg;
        return this;
    }

    @Column(name = "international_minutes", nullable = false)
    public String getInternationalMinutes() {
        return internationalMinutes;
    }

    public FixedVoicePlanEntity setInternationalMinutes(String internationalMinutes) {
        this.internationalMinutes = internationalMinutes;
        return this;
    }
}
