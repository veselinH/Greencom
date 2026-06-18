package bg.greencom.loyalty.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_accounts")
public class LoyaltyAccount {

    private Long id;
    private String username;
    private int pointsBalance;
    private int totalEarned;
    private LocalDateTime updatedOn;

    public LoyaltyAccount() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public LoyaltyAccount setId(Long id) {
        this.id = id;
        return this;
    }

    @Column(nullable = false, unique = true)
    public String getUsername() {
        return username;
    }

    public LoyaltyAccount setUsername(String username) {
        this.username = username;
        return this;
    }

    @Column(name = "points_balance", nullable = false)
    public int getPointsBalance() {
        return pointsBalance;
    }

    public LoyaltyAccount setPointsBalance(int pointsBalance) {
        this.pointsBalance = pointsBalance;
        return this;
    }

    @Column(name = "total_earned", nullable = false)
    public int getTotalEarned() {
        return totalEarned;
    }

    public LoyaltyAccount setTotalEarned(int totalEarned) {
        this.totalEarned = totalEarned;
        return this;
    }

    @Column(name = "updated_on")
    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public LoyaltyAccount setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
        return this;
    }
}
