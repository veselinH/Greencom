package bg.greencom.greencomwebapp.model.view;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class ContractViewModel {

    private Long id;
    private Long planId;
    private Long userId;
    private Set<AdditionalPackageViewModel> additionalPackageViewModels = new HashSet<>();
    private LocalDate signedOn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public Set<AdditionalPackageViewModel> getAdditionalPackageViewModels() {
        return additionalPackageViewModels;
    }

    public void setAdditionalPackageViewModels(Set<AdditionalPackageViewModel> additionalPackageViewModels) {
        this.additionalPackageViewModels = additionalPackageViewModels;
    }

    public LocalDate getSignedOn() {
        return signedOn;
    }

    public void setSignedOn(LocalDate signedOn) {
        this.signedOn = signedOn;
    }
}
