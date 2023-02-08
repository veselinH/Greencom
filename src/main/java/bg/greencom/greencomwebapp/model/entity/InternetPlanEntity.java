package bg.greencom.greencomwebapp.model.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "internet_plans")
public class InternetPlanEntity extends PlanEntity {

    private Integer downloadMbps;
    private Integer uploadMbps;
    private InternetTypeEntity internetType;
    private Set<InternetExtrasEntity> internetExtras;

    public InternetPlanEntity() {
    }

    @Column(name = "download_mbps", nullable = false)
    public Integer getDownloadMbps() {
        return downloadMbps;
    }

    public InternetPlanEntity setDownloadMbps(Integer downloadMbps) {
        this.downloadMbps = downloadMbps;
        return this;
    }

    @Column(name = "upload_mbps", nullable = false)
    public Integer getUploadMbps() {
        return uploadMbps;
    }

    public InternetPlanEntity setUploadMbps(Integer uploadMbps) {
        this.uploadMbps = uploadMbps;
        return this;
    }

    @ManyToOne
    public InternetTypeEntity getInternetType() {
        return internetType;
    }

    public InternetPlanEntity setInternetType(InternetTypeEntity internetType) {
        this.internetType = internetType;
        return this;
    }

    @OneToMany
    public Set<InternetExtrasEntity> getInternetExtras() {
        return internetExtras;
    }

    public InternetPlanEntity setInternetExtras(Set<InternetExtrasEntity> internetExtras) {
        this.internetExtras = internetExtras;
        return this;
    }
}

