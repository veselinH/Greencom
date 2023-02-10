package bg.greencom.greencomwebapp.model.view;

import bg.greencom.greencomwebapp.model.entity.MobileExtraEntity;

import java.math.BigDecimal;
import java.util.*;

public class VoicePlanViewModel {

    private Long id;
    private String name;
    private String planDuration;
    private String bgMinutes;
    private String roamingMinutes;
    private String bgInternetMegabytes;
    private String roamingInternetMegabytes;
    private BigDecimal price;
    private List<MobileExtraEntity> mobileExtras = new ArrayList<>();


    public Long getId() {
        return id;
    }

    public VoicePlanViewModel setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public VoicePlanViewModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getPlanDuration() {
        return planDuration;
    }

    public VoicePlanViewModel setPlanDuration(String planDuration) {
        this.planDuration = planDuration;
        return this;
    }

    public String getBgMinutes() {
        return bgMinutes;
    }

    public VoicePlanViewModel setBgMinutes(String bgMinutes) {
        this.bgMinutes = bgMinutes;
        return this;
    }

    public String getRoamingMinutes() {
        return roamingMinutes;
    }

    public VoicePlanViewModel setRoamingMinutes(String roamingMinutes) {
        this.roamingMinutes = roamingMinutes;
        return this;
    }

    public String getBgInternetMegabytes() {
        return bgInternetMegabytes;
    }

    public VoicePlanViewModel setBgInternetMegabytes(String bgInternetMegabytes) {
        this.bgInternetMegabytes = bgInternetMegabytes;
        return this;
    }

    public String getRoamingInternetMegabytes() {
        return roamingInternetMegabytes;
    }

    public VoicePlanViewModel setRoamingInternetMegabytes(String roamingInternetMegabytes) {
        this.roamingInternetMegabytes = roamingInternetMegabytes;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public VoicePlanViewModel setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public List<MobileExtraEntity> getMobileExtras() {
        return mobileExtras;
    }

    public VoicePlanViewModel setMobileExtras(List<MobileExtraEntity> mobileExtras) {
        this.mobileExtras = mobileExtras;
        return this;
    }
}
