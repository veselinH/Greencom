package bg.greencom.greencomwebapp.model.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;


@Entity
@Table(name = "plans")
@Inheritance(strategy = InheritanceType.JOINED)
public class PlanEntity extends BaseEntity {

    private String name;
    private String planDuration;
    private BigDecimal price;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;
    private boolean isActive;

    public PlanEntity() {
    }

    @Column(nullable = false)
    public String getName() {
        return name;
    }

    public PlanEntity setName(String name) {
        this.name = name;
        return this;
    }

    @Column(name = "plan_duration")
    public String getPlanDuration() {
        return planDuration;
    }

    public PlanEntity setPlanDuration(String planDuration) {
        this.planDuration = planDuration;
        return this;
    }

    @Column(nullable = false)
    public BigDecimal getPrice() {
        return price;
    }

    public PlanEntity setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    @Column(name = "created_on", nullable = false)
    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public PlanEntity setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
        return this;
    }

    @Column(name = "modified_on")
    public LocalDateTime getModifiedOn() {
        return modifiedOn;
    }

    public PlanEntity setModifiedOn(LocalDateTime modifiedOn) {
        this.modifiedOn = modifiedOn;
        return this;
    }

    @Column(name = "is_active")
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Human-readable label for the concrete plan type, shown on the contract PDF.
     * Subtypes override to identify themselves.
     */
    @Transient
    public String getPlanType() {
        return "Service";
    }

    /**
     * Type-specific fields to render on the contract PDF, as an ordered map of
     * label to display value. Subtypes override to expose their own fields.
     */
    @Transient
    public Map<String, String> getPlanDetails() {
        return new LinkedHashMap<>();
    }
}
