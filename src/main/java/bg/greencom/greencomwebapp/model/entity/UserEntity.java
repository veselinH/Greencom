package bg.greencom.greencomwebapp.model.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity {

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime registeredOn;
    private BigDecimal totalDebtPerMonth;
    private Set<UserRoleEntity> roles = new HashSet<>();
    private List<InternetPlanEntity> userInternetPlans;
    private List<VoicePlanEntity> userVoiceMobilePlans;
    private List<DataPlanEntity> userDataPlans;
    private List<TelevisionPlanEntity> userTelevisionPlans;
    private List<FixedVoicePlanEntity> userFixedVoicePlans;

    public UserEntity() {
    }

    @Column(nullable = false, unique = true)
    public String getUsername() {
        return username;
    }

    public UserEntity setUsername(String username) {
        this.username = username;
        return this;
    }

    @Column(nullable = false)
    public String getPassword() {
        return password;
    }

    public UserEntity setPassword(String password) {
        this.password = password;
        return this;
    }

    @Column(name = "first_name", nullable = false)
    public String getFirstName() {
        return firstName;
    }

    public UserEntity setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    @Column(name = "last_name", nullable = false)
    public String getLastName() {
        return lastName;
    }

    public UserEntity setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    @Column
    public String getEmail() {
        return email;
    }

    public UserEntity setEmail(String email) {
        this.email = email;
        return this;
    }

    @Column(name = "registered_on")
    public LocalDateTime getRegisteredOn() {
        return registeredOn;
    }

    public UserEntity setRegisteredOn(LocalDateTime registeredOn) {
        this.registeredOn = registeredOn;
        return this;
    }

    @Column(name = "total_debt_per_month")
    public BigDecimal getTotalDebtPerMonth() {
        return totalDebtPerMonth;
    }

    public UserEntity setTotalDebtPerMonth(BigDecimal totalDebtPerMonth) {
        this.totalDebtPerMonth = totalDebtPerMonth;
        return this;
    }

    @OneToMany
    public Set<UserRoleEntity> getRoles() {
        return roles;
    }

    public UserEntity setRoles(Set<UserRoleEntity> roles) {
        this.roles = roles;
        return this;
    }

    @OneToMany
    public List<InternetPlanEntity> getUserInternetPlans() {
        return userInternetPlans;
    }

    public UserEntity setUserInternetPlans(List<InternetPlanEntity> userInternetPlans) {
        this.userInternetPlans = userInternetPlans;
        return this;
    }

    @OneToMany
    public List<VoicePlanEntity> getUserVoiceMobilePlans() {
        return userVoiceMobilePlans;
    }

    public UserEntity setUserVoiceMobilePlans(List<VoicePlanEntity> userVoiceMobilePlans) {
        this.userVoiceMobilePlans = userVoiceMobilePlans;
        return this;
    }

    @OneToMany
    public List<DataPlanEntity> getUserDataPlans() {
        return userDataPlans;
    }

    public UserEntity setUserDataPlans(List<DataPlanEntity> userDataPlans) {
        this.userDataPlans = userDataPlans;
        return this;
    }

    @OneToMany
    public List<TelevisionPlanEntity> getUserTelevisionPlans() {
        return userTelevisionPlans;
    }

    public UserEntity setUserTelevisionPlans(List<TelevisionPlanEntity> userTelevisionPlans) {
        this.userTelevisionPlans = userTelevisionPlans;
        return this;
    }

    @OneToMany
    public List<FixedVoicePlanEntity> getUserFixedVoicePlans() {
        return userFixedVoicePlans;
    }

    public UserEntity setUserFixedVoicePlans(List<FixedVoicePlanEntity> userFixedVoicePlans) {
        this.userFixedVoicePlans = userFixedVoicePlans;
        return this;
    }
}
