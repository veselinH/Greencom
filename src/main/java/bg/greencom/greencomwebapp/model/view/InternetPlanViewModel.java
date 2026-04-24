package bg.greencom.greencomwebapp.model.view;

import bg.greencom.greencomwebapp.model.entity.InternetExtrasEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InternetPlanViewModel {

    private Long id;
    private String name;
    private String planDuration;
    private Integer downloadMbps;
    private Integer uploadMbps;
    private String internetType;
    private BigDecimal price;
    private List<InternetExtrasEntity> internetExtras = new ArrayList<>();

    private Long contractId;

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

    public String getInternetType() {
        return internetType;
    }

    public void setInternetType(String internetType) {
        this.internetType = internetType;
    }

    public List<InternetExtrasEntity> getInternetExtras() {
        return internetExtras;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setInternetExtras(List<InternetExtrasEntity> internetExtras) {
        this.internetExtras = internetExtras;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }
}
