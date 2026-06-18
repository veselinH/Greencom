package bg.greencom.loyalty.scheduling;

import bg.greencom.loyalty.service.LoyaltyService;
import bg.greencom.loyalty.service.impl.LoyaltyServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled jobs for the loyalty program.
 *
 * <ul>
 *   <li><b>Cron job</b> ({@link #awardMonthlyTierBonus()}) — awards a tier-based
 *       bonus to every account on a cron schedule. Changes domain state, so it
 *       also evicts the accounts cache (via the service method).</li>
 *   <li><b>Fixed-delay job</b> ({@link #evictAccountsCache()}) — periodically
 *       clears the accounts cache, giving the in-memory cache an effective TTL.</li>
 * </ul>
 */
@Component
public class LoyaltyScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoyaltyScheduler.class);

    private final LoyaltyService loyaltyService;

    public LoyaltyScheduler(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    /**
     * Cron-triggered: award the monthly tier bonus to all accounts.
     * Schedule is configured by {@code loyalty.scheduling.bonus-cron}.
     */
    @Scheduled(cron = "${loyalty.scheduling.bonus-cron}")
    public void awardMonthlyTierBonus() {
        int affected = loyaltyService.awardTierBonusToAll();
        LOGGER.info("[CRON] Monthly tier bonus awarded to {} loyalty account(s).", affected);
    }

    /**
     * Fixed-delay-triggered: evict the accounts cache so cached balances cannot
     * go stale indefinitely. Delay is configured by
     * {@code loyalty.scheduling.cache-evict-delay-ms}.
     */
    @Scheduled(fixedDelayString = "${loyalty.scheduling.cache-evict-delay-ms}")
    @CacheEvict(value = LoyaltyServiceImpl.ACCOUNTS_CACHE, allEntries = true)
    public void evictAccountsCache() {
        LOGGER.info("[FIXED-DELAY] Cleared all entries from '{}' cache.", LoyaltyServiceImpl.ACCOUNTS_CACHE);
    }
}
