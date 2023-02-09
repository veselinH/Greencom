package bg.greencom.greencomwebapp.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;


@MappedSuperclass
public abstract class PlanEntity extends BaseEntity {

    private String name;
    private String planDuration;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;


    public PlanEntity() {
    }

    @Column(nullable = false, unique = true)
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
}
