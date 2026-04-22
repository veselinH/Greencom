package bg.greencom.greencomwebapp.model.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "contracts")
public class ContractEntity extends BaseEntity{

    private UserEntity user;
    private PlanEntity plan;
    private LocalDate signedOn;
    private LocalDate unsignedOn;
    private byte[] signSignature;
    private byte[] unsignSignature;
    private boolean isActive;

    public ContractEntity() {
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    public UserEntity getUser() {
        return user;
    }

    public ContractEntity setUser(UserEntity user) {
        this.user = user;
        return this;
    }

    @ManyToOne
    @JoinColumn(name = "plan_id")
    public PlanEntity getPlan() {
        return plan;
    }

    public ContractEntity setPlan(PlanEntity plan) {
        this.plan = plan;
        return this;
    }

    @Column(name = "signed_on",nullable = false)
    public LocalDate getSignedOn() {
        return signedOn;
    }

    public ContractEntity setSignedOn(LocalDate signedOn) {
        this.signedOn = signedOn;
        return this;
    }

    @Column(name = "unsigned_on")
    public LocalDate getUnsignedOn() {
        return unsignedOn;
    }

    public ContractEntity setUnsignedOn(LocalDate unsignedOn) {
        this.unsignedOn = unsignedOn;
        return this;
    }

    //    Saved as BLOB - Binary Large Object
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "sign_signature", columnDefinition = "LONGBLOB")
    public byte[] getSignSignature() {
        return signSignature;
    }

    public void setSignSignature(byte[] signSignature) {
        this.signSignature = signSignature;
    }

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "unsign_signature", columnDefinition = "LONGBLOB")
    public byte[] getUnsignSignature() {
        return unsignSignature;
    }

    public ContractEntity setUnsignSignature(byte[] unsignSignature) {
        this.unsignSignature = unsignSignature;
        return this;
    }

    @Column(name = "is_active")
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
