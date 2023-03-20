package bg.greencom.greencomwebapp.model.view;

import bg.greencom.greencomwebapp.model.entity.MobileExtraEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DataPlanViewModel {
    private Long id;
    private String name;
    private String planDuration;
    private String bgInternetMegabytes;
    private String roamingInternetMegabytes;
    private BigDecimal price;
    private List<MobileExtraEntity> mobileExtras = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public DataPlanViewModel setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public DataPlanViewModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getPlanDuration() {
        return planDuration;
    }

    public DataPlanViewModel setPlanDuration(String planDuration) {
        this.planDuration = planDuration;
        return this;
    }

    public String getBgInternetMegabytes() {
        return bgInternetMegabytes;
    }

    public DataPlanViewModel setBgInternetMegabytes(String bgInternetMegabytes) {
        this.bgInternetMegabytes = bgInternetMegabytes;
        return this;
    }

    public String getRoamingInternetMegabytes() {
        return roamingInternetMegabytes;
    }

    public DataPlanViewModel setRoamingInternetMegabytes(String roamingInternetMegabytes) {
        this.roamingInternetMegabytes = roamingInternetMegabytes;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public DataPlanViewModel setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public List<MobileExtraEntity> getMobileExtras() {
        return mobileExtras;
    }

    public DataPlanViewModel setMobileExtras(List<MobileExtraEntity> mobileExtras) {
        this.mobileExtras = mobileExtras;
        return this;
    }
}
