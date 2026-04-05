package bg.greencom.greencomwebapp.model.service;

import bg.greencom.greencomwebapp.model.entity.enums.InternetExtraEnum;
import bg.greencom.greencomwebapp.model.entity.enums.InternetTypeEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InternetPlanServiceModel {
    private Long id;
    private String name;
    private String planDuration;
    private Integer downloadMbps;
    private Integer uploadMbps;
    private BigDecimal price;
    private InternetTypeEnum internetType;
    private List<InternetExtraEnum> internetExtras = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlanDuration() {
        return planDuration;
    }

    public void setPlanDuration(String planDuration) {
        this.planDuration = planDuration;
    }

    public Integer getDownloadMbps() {
        return downloadMbps;
    }

    public void setDownloadMbps(Integer downloadMbps) {
        this.downloadMbps = downloadMbps;
    }

    public Integer getUploadMbps() {
        return uploadMbps;
    }

    public void setUploadMbps(Integer uploadMbps) {
        this.uploadMbps = uploadMbps;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public InternetTypeEnum getInternetType() {
        return internetType;
    }

    public void setInternetType(InternetTypeEnum internetType) {
        this.internetType = internetType;
    }

    public List<InternetExtraEnum> getInternetExtras() {
        return internetExtras;
    }

    public void setInternetExtras(List<InternetExtraEnum> internetExtras) {
        this.internetExtras = internetExtras;
    }
}
