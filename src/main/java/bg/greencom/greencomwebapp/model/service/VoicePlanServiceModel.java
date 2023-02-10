package bg.greencom.greencomwebapp.model.service;

import bg.greencom.greencomwebapp.model.entity.enums.MobileExtraEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class VoicePlanServiceModel {

    private Long id;
    private String name;
    private String planDuration;
    private String bgMinutes;
    private String roamingMinutes;
    private String bgInternetMegabytes;
    private String roamingInternetMegabytes;
    private BigDecimal price;
    private List<MobileExtraEnum> mobileExtras = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public VoicePlanServiceModel setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public VoicePlanServiceModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getPlanDuration() {
        return planDuration;
    }

    public VoicePlanServiceModel setPlanDuration(String planDuration) {
        this.planDuration = planDuration;
        return this;
    }

    public String getBgMinutes() {
        return bgMinutes;
    }

    public VoicePlanServiceModel setBgMinutes(String bgMinutes) {
        this.bgMinutes = bgMinutes;
        return this;
    }

    public String getRoamingMinutes() {
        return roamingMinutes;
    }

    public VoicePlanServiceModel setRoamingMinutes(String roamingMinutes) {
        this.roamingMinutes = roamingMinutes;
        return this;
    }

    public String getBgInternetMegabytes() {
        return bgInternetMegabytes;
    }

    public VoicePlanServiceModel setBgInternetMegabytes(String bgInternetMegabytes) {
        this.bgInternetMegabytes = bgInternetMegabytes;
        return this;
    }

    public String getRoamingInternetMegabytes() {
        return roamingInternetMegabytes;
    }

    public VoicePlanServiceModel setRoamingInternetMegabytes(String roamingInternetMegabytes) {
        this.roamingInternetMegabytes = roamingInternetMegabytes;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public VoicePlanServiceModel setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public List<MobileExtraEnum> getMobileExtras() {
        return mobileExtras;
    }

    public VoicePlanServiceModel setMobileExtras(List<MobileExtraEnum> mobileExtras) {
        this.mobileExtras = mobileExtras;
        return this;
    }
}
