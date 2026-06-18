package bg.greencom.greencomwebapp.model.view;

import java.math.BigDecimal;

public class UserViewModel {

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private BigDecimal totalDebtPerMonth;
    private int loyaltyPoints;
    private String loyaltyTier;

    public String getFirstName() {
        return firstName;
    }

    public UserViewModel setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public UserViewModel setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserViewModel setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserViewModel setEmail(String email) {
        this.email = email;
        return this;
    }

    public BigDecimal getTotalDebtPerMonth() {
        return totalDebtPerMonth;
    }

    public UserViewModel setTotalDebtPerMonth(BigDecimal totalDebtPerMonth) {
        this.totalDebtPerMonth = totalDebtPerMonth;
        return this;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public UserViewModel setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
        return this;
    }

    public String getLoyaltyTier() {
        return loyaltyTier;
    }

    public UserViewModel setLoyaltyTier(String loyaltyTier) {
        this.loyaltyTier = loyaltyTier;
        return this;
    }
}
