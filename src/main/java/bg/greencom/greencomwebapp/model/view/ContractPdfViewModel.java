package bg.greencom.greencomwebapp.model.view;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ContractPdfViewModel {

    private Long id;
    private String firstName;
    private String lastName;
    private String planName;
    private BigDecimal price;
    private String planDuration;
    private LocalDate signedOn;

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
}
