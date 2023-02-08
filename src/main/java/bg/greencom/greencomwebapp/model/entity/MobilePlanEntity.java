package bg.greencom.greencomwebapp.model.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Set;

@MappedSuperclass
public class MobilePlanEntity extends PlanEntity {

    private String bgInternetMegabytes;
    private String roamingInternetMegabytes;
    private BigDecimal price;
    private Set<MobileExtraEntity> mobileExtras;

    public MobilePlanEntity() {
    }

    @Column(name = "bg_internet_megabytes", nullable = false)
    public String getBgInternetMegabytes() {
        return bgInternetMegabytes;
    }

    public MobilePlanEntity setBgInternetMegabytes(String bgInternetMegabytes) {
        this.bgInternetMegabytes = bgInternetMegabytes;
        return this;
    }

    @Column(name = "roaming_internet_megabytes")
    public String getRoamingInternetMegabytes() {
        return roamingInternetMegabytes;
    }

    public MobilePlanEntity setRoamingInternetMegabytes(String roamingInternetMegabytes) {
        this.roamingInternetMegabytes = roamingInternetMegabytes;
        return this;
    }

    @Column(nullable = false)
    public BigDecimal getPrice() {
        return price;
    }

    public MobilePlanEntity setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    @OneToMany
    public Set<MobileExtraEntity> getMobileExtras() {
        return mobileExtras;
    }

    public MobilePlanEntity setMobileExtras(Set<MobileExtraEntity> mobileExtras) {
        this.mobileExtras = mobileExtras;
        return this;
    }
}
