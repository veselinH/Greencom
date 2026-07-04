package bg.greencom.greencomwebapp.model.view;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ContractPdfViewModel {

    private Long id;
    private String firstName;
    private String lastName;
    private String planName;
    private String planType;
    private BigDecimal price;
    private String planDuration;
    private LocalDate signedOn;
    private Map<String, String> planDetails = new LinkedHashMap<>();
    private Set<AdditionalPackageViewModel> additionalPackages = new HashSet<>();

    public Long getId() {
        return id;
    }

    public ContractPdfViewModel setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public ContractPdfViewModel setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public ContractPdfViewModel setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getPlanName() {
        return planName;
    }

    public ContractPdfViewModel setPlanName(String planName) {
        this.planName = planName;
        return this;
    }

    public String getPlanType() {
        return planType;
    }

    public ContractPdfViewModel setPlanType(String planType) {
        this.planType = planType;
        return this;
    }

    public Map<String, String> getPlanDetails() {
        return planDetails;
    }

    public ContractPdfViewModel setPlanDetails(Map<String, String> planDetails) {
        this.planDetails = planDetails;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public ContractPdfViewModel setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public String getPlanDuration() {
        return planDuration;
    }

    public ContractPdfViewModel setPlanDuration(String planDuration) {
        this.planDuration = planDuration;
        return this;
    }

    public LocalDate getSignedOn() {
        return signedOn;
    }

    public ContractPdfViewModel setSignedOn(LocalDate signedOn) {
        this.signedOn = signedOn;
        return this;
    }

    public Set<AdditionalPackageViewModel> getAdditionalPackages() {
        return additionalPackages;
    }

    public ContractPdfViewModel setAdditionalPackages(Set<AdditionalPackageViewModel> additionalPackages) {
        this.additionalPackages = additionalPackages;
        return this;
    }
}
