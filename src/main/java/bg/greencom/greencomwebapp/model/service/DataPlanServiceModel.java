package bg.greencom.greencomwebapp.model.service;

import bg.greencom.greencomwebapp.model.entity.enums.MobileExtraEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DataPlanServiceModel {

    private Long id;
    private String name;
    private String planDuration;
    private String bgInternetMegabytes;
    private String roamingInternetMegabytes;
    private BigDecimal price;
    private List<MobileExtraEnum> mobileExtras = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public DataPlanServiceModel setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public DataPlanServiceModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getPlanDuration() {
        return planDuration;
    }

    public DataPlanServiceModel setPlanDuration(String planDuration) {
        this.planDuration = planDuration;
        return this;
    }

    public String getBgInternetMegabytes() {
        return bgInternetMegabytes;
    }

    public DataPlanServiceModel setBgInternetMegabytes(String bgInternetMegabytes) {
        this.bgInternetMegabytes = bgInternetMegabytes;
        return this;
    }

    public String getRoamingInternetMegabytes() {
        return roamingInternetMegabytes;
    }

    public DataPlanServiceModel setRoamingInternetMegabytes(String roamingInternetMegabytes) {
        this.roamingInternetMegabytes = roamingInternetMegabytes;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public DataPlanServiceModel setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public List<MobileExtraEnum> getMobileExtras() {
        return mobileExtras;
    }

    public DataPlanServiceModel setMobileExtras(List<MobileExtraEnum> mobileExtras) {
        this.mobileExtras = mobileExtras;
        return this;
    }
}
