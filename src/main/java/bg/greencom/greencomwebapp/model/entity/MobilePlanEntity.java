package bg.greencom.greencomwebapp.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MappedSuperclass;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@MappedSuperclass
public abstract class MobilePlanEntity extends PlanEntity {

    private String bgInternetMegabytes;
    private String roamingInternetMegabytes;
    private BigDecimal price;
    private List<MobileExtraEntity> mobileExtras = new ArrayList<>();

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

    @ManyToMany(fetch = FetchType.EAGER)
    public List<MobileExtraEntity> getMobileExtras() {
        return mobileExtras;
    }

    public MobilePlanEntity setMobileExtras(List<MobileExtraEntity> mobileExtras) {
        this.mobileExtras = mobileExtras;
        return this;
    }
}
