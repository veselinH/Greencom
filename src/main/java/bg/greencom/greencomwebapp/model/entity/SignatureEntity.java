package bg.greencom.greencomwebapp.model.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "signatures")
public class SignatureEntity extends BaseEntity{

    private UserEntity user;
    private PlanEntity plan;
    private LocalDate signedOn;
    private byte[] signature;

    public SignatureEntity() {
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    public UserEntity getUser() {
        return user;
    }

    public SignatureEntity setUser(UserEntity user) {
        this.user = user;
        return this;
    }

    @ManyToOne
    @JoinColumn(name = "plan_id")
    public PlanEntity getPlan() {
        return plan;
    }

    public SignatureEntity setPlan(PlanEntity plan) {
        this.plan = plan;
        return this;
    }

    @Column(name = "signed_on",nullable = false)
    public LocalDate getSignedOn() {
        return signedOn;
    }

    public SignatureEntity setSignedOn(LocalDate signedOn) {
        this.signedOn = signedOn;
        return this;
    }

    //    Saved as BLOB - Binary Large Object
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "signature", columnDefinition = "LONGBLOB")
    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }
}
