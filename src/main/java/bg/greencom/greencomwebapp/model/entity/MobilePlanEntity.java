package bg.greencom.greencomwebapp.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@MappedSuperclass
public abstract class MobilePlanEntity extends PlanEntity {

    private String bgInternetMegabytes;
    private String roamingInternetMegabytes;
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

    @ManyToMany(fetch = FetchType.EAGER)
    public List<MobileExtraEntity> getMobileExtras() {
        return mobileExtras;
    }

    public MobilePlanEntity setMobileExtras(List<MobileExtraEntity> mobileExtras) {
        this.mobileExtras = mobileExtras;
        return this;
    }

    @Override
    @Transient
    public String getPlanType() {
        return "Mobile";
    }

    @Override
    @Transient
    public Map<String, String> getPlanDetails() {
        Map<String, String> details = new LinkedHashMap<>();
        details.put("BG Internet (MB)", bgInternetMegabytes);
        if (roamingInternetMegabytes != null) {
            details.put("Roaming Internet (MB)", roamingInternetMegabytes);
        }
        return details;
    }
}
