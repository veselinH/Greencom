package bg.greencom.greencomwebapp.model.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "plans")
@Inheritance(strategy = InheritanceType.JOINED)
public class PlanEntity extends BaseEntity {

    private String name;
    private String planDuration;
    private BigDecimal price;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;

//    private List<ContractEntity> contract;

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

//    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
//    public List<ContractEntity> getContract() {
//        return contract;
//    }
//
//    public void setSignature(List<ContractEntity> signature) {
//        this.contract = contract;
//    }

}
