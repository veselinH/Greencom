package bg.greencom.loyalty.service.impl;

import bg.greencom.loyalty.dto.LoyaltyResponse;
import bg.greencom.loyalty.model.LoyaltyAccount;
import bg.greencom.loyalty.repository.LoyaltyAccountRepository;
import bg.greencom.loyalty.service.InsufficientPointsException;
import bg.greencom.loyalty.service.LoyaltyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoyaltyServiceImpl implements LoyaltyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoyaltyServiceImpl.class);

    public static final String ACCOUNTS_CACHE = "loyaltyAccounts";

    private static final int POINTS_PER_BGN = 100;

    private static final int SILVER_THRESHOLD = 500;
    private static final int GOLD_THRESHOLD = 1500;

    private static final int BRONZE_BONUS = 10;
    private static final int SILVER_BONUS = 50;
    private static final int GOLD_BONUS = 100;

    private final LoyaltyAccountRepository loyaltyAccountRepository;

    public LoyaltyServiceImpl(LoyaltyAccountRepository loyaltyAccountRepository) {
        this.loyaltyAccountRepository = loyaltyAccountRepository;
    }

    @Override
    @Transactional
    @Cacheable(value = ACCOUNTS_CACHE, key = "#username")
    public LoyaltyResponse getAccount(String username) {
        return toResponse(getOrCreate(username), BigDecimal.ZERO);
    }

    @Override
    @Transactional
    @CacheEvict(value = ACCOUNTS_CACHE, key = "#username")
    public LoyaltyResponse earn(String username, int points) {
        LoyaltyAccount account = getOrCreate(username);
        account
                .setPointsBalance(account.getPointsBalance() + points)
                .setTotalEarned(account.getTotalEarned() + points)
                .setUpdatedOn(LocalDateTime.now());

        LoyaltyAccount saved = loyaltyAccountRepository.saveAndFlush(account);
        LOGGER.info("User {} earned {} loyalty points; new balance is {}.",
                username, points, saved.getPointsBalance());

        return toResponse(saved, BigDecimal.ZERO);
    }

    @Override
    @Transactional
    @CacheEvict(value = ACCOUNTS_CACHE, key = "#username")
    public LoyaltyResponse redeem(String username, int points) {
        LoyaltyAccount account = getOrCreate(username);

        if (points > account.getPointsBalance()) {
            LOGGER.warn("User {} attempted to redeem {} points with balance {}.",
                    username, points, account.getPointsBalance());
            throw new InsufficientPointsException(
                    "Cannot redeem " + points + " points; balance is " + account.getPointsBalance() + ".");
        }

        account
                .setPointsBalance(account.getPointsBalance() - points)
                .setUpdatedOn(LocalDateTime.now());

        BigDecimal discountBgn = BigDecimal.valueOf(points)
                .divide(BigDecimal.valueOf(POINTS_PER_BGN));

        LoyaltyAccount saved = loyaltyAccountRepository.saveAndFlush(account);
        LOGGER.info("User {} redeemed {} loyalty points for {} BGN discount; new balance is {}.",
                username, points, discountBgn, saved.getPointsBalance());

        return toResponse(saved, discountBgn);
    }

    @Override
    @Transactional
    @CacheEvict(value = ACCOUNTS_CACHE, key = "#username")
    public LoyaltyResponse revoke(String username, int amount) {
        LoyaltyAccount account = getOrCreate(username);

        int newBalance = Math.max(0, account.getPointsBalance() - amount);
        account
                .setPointsBalance(newBalance)
                .setUpdatedOn(LocalDateTime.now());

        LoyaltyAccount saved = loyaltyAccountRepository.saveAndFlush(account);
        LOGGER.info("Revoked {} loyalty points from user {}; new balance is {}.",
                amount, username, saved.getPointsBalance());

        return toResponse(saved, BigDecimal.ZERO);
    }

    @Override
    @Transactional
    @CacheEvict(value = ACCOUNTS_CACHE, allEntries = true)
    public int awardTierBonusToAll() {
        List<LoyaltyAccount> accounts = loyaltyAccountRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (LoyaltyAccount account : accounts) {
            int bonus = bonusForTier(account.getTotalEarned());
            account
                    .setPointsBalance(account.getPointsBalance() + bonus)
                    .setTotalEarned(account.getTotalEarned() + bonus)
                    .setUpdatedOn(now);
        }

        loyaltyAccountRepository.saveAll(accounts);
        return accounts.size();
    }

    private int bonusForTier(int totalEarned) {
        if (totalEarned >= GOLD_THRESHOLD) {
            return GOLD_BONUS;
        }
        if (totalEarned >= SILVER_THRESHOLD) {
            return SILVER_BONUS;
        }
        return BRONZE_BONUS;
    }

    private LoyaltyAccount getOrCreate(String username) {
        return loyaltyAccountRepository
                .findByUsername(username)
                .orElseGet(() -> {
                    LOGGER.info("Created new loyalty account for user {}.", username);
                    return loyaltyAccountRepository.saveAndFlush(
                            new LoyaltyAccount()
                                    .setUsername(username)
                                    .setPointsBalance(0)
                                    .setTotalEarned(0)
                                    .setUpdatedOn(LocalDateTime.now()));
                });
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
