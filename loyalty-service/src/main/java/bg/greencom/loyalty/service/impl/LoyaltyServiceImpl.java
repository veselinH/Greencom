package bg.greencom.loyalty.service.impl;

import bg.greencom.loyalty.dto.LoyaltyResponse;
import bg.greencom.loyalty.model.LoyaltyAccount;
import bg.greencom.loyalty.repository.LoyaltyAccountRepository;
import bg.greencom.loyalty.service.InsufficientPointsException;
import bg.greencom.loyalty.service.LoyaltyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class LoyaltyServiceImpl implements LoyaltyService {

    // 100 points redeem into 1.00 BGN of discount.
    private static final int POINTS_PER_BGN = 100;

    private static final int SILVER_THRESHOLD = 500;
    private static final int GOLD_THRESHOLD = 1500;

    private final LoyaltyAccountRepository loyaltyAccountRepository;

    public LoyaltyServiceImpl(LoyaltyAccountRepository loyaltyAccountRepository) {
        this.loyaltyAccountRepository = loyaltyAccountRepository;
    }

    @Override
    @Transactional
    public LoyaltyResponse getAccount(String username) {
        return toResponse(getOrCreate(username), BigDecimal.ZERO);
    }

    @Override
    @Transactional
    public LoyaltyResponse earn(String username, int points) {
        LoyaltyAccount account = getOrCreate(username);
        account
                .setPointsBalance(account.getPointsBalance() + points)
                .setTotalEarned(account.getTotalEarned() + points)
                .setUpdatedOn(LocalDateTime.now());

        return toResponse(loyaltyAccountRepository.saveAndFlush(account), BigDecimal.ZERO);
    }

    @Override
    @Transactional
    public LoyaltyResponse redeem(String username, int points) {
        LoyaltyAccount account = getOrCreate(username);

        if (points > account.getPointsBalance()) {
            throw new InsufficientPointsException(
                    "Cannot redeem " + points + " points; balance is " + account.getPointsBalance() + ".");
        }

        account
                .setPointsBalance(account.getPointsBalance() - points)
                .setUpdatedOn(LocalDateTime.now());

        BigDecimal discountBgn = BigDecimal.valueOf(points)
                .divide(BigDecimal.valueOf(POINTS_PER_BGN));

        return toResponse(loyaltyAccountRepository.saveAndFlush(account), discountBgn);
    }

    @Override
    @Transactional
    public LoyaltyResponse revoke(String username, int amount) {
        LoyaltyAccount account = getOrCreate(username);

        int newBalance = Math.max(0, account.getPointsBalance() - amount);
        account
                .setPointsBalance(newBalance)
                .setUpdatedOn(LocalDateTime.now());

        return toResponse(loyaltyAccountRepository.saveAndFlush(account), BigDecimal.ZERO);
    }

    private LoyaltyAccount getOrCreate(String username) {
        return loyaltyAccountRepository
                .findByUsername(username)
                .orElseGet(() -> loyaltyAccountRepository.saveAndFlush(
                        new LoyaltyAccount()
                                .setUsername(username)
                                .setPointsBalance(0)
                                .setTotalEarned(0)
                                .setUpdatedOn(LocalDateTime.now())));
    }

    private LoyaltyResponse toResponse(LoyaltyAccount account, BigDecimal discountBgn) {
        return new LoyaltyResponse()
                .setUsername(account.getUsername())
                .setPointsBalance(account.getPointsBalance())
                .setTotalEarned(account.getTotalEarned())
                .setTier(resolveTier(account.getTotalEarned()))
                .setDiscountBgn(discountBgn);
    }

    private String resolveTier(int totalEarned) {
        if (totalEarned >= GOLD_THRESHOLD) {
            return "GOLD";
        }
        if (totalEarned >= SILVER_THRESHOLD) {
            return "SILVER";
        }
        return "BRONZE";
    }
}
