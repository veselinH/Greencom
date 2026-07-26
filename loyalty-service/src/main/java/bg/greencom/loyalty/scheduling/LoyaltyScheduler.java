package bg.greencom.loyalty.scheduling;

import bg.greencom.loyalty.service.LoyaltyService;
import bg.greencom.loyalty.service.impl.LoyaltyServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LoyaltyScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoyaltyScheduler.class);

    private final LoyaltyService loyaltyService;

    public LoyaltyScheduler(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    @Scheduled(cron = "${loyalty.scheduling.bonus-cron}")
    public void awardMonthlyTierBonus() {
        int affected = loyaltyService.awardTierBonusToAll();
        LOGGER.info("[CRON] Monthly tier bonus awarded to {} loyalty account(s).", affected);
    }

    @Scheduled(fixedDelayString = "${loyalty.scheduling.cache-evict-delay-ms}")
    @CacheEvict(value = LoyaltyServiceImpl.ACCOUNTS_CACHE, allEntries = true)
    public void evictAccountsCache() {
        LOGGER.info("[FIXED-DELAY] Cleared all entries from '{}' cache.", LoyaltyServiceImpl.ACCOUNTS_CACHE);
    }
}
