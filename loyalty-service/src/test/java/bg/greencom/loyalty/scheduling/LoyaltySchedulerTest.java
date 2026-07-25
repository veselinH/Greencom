package bg.greencom.loyalty.scheduling;

import bg.greencom.loyalty.service.LoyaltyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoyaltySchedulerTest {

    @Mock
    private LoyaltyService loyaltyService;

    @InjectMocks
    private LoyaltyScheduler loyaltyScheduler;

    @Test
    void awardMonthlyTierBonus_delegatesToService() {
        when(loyaltyService.awardTierBonusToAll()).thenReturn(3);

        loyaltyScheduler.awardMonthlyTierBonus();

        verify(loyaltyService).awardTierBonusToAll();
        verifyNoMoreInteractions(loyaltyService);
    }

    @Test
    void evictAccountsCache_doesNotTouchTheService() {
        loyaltyScheduler.evictAccountsCache();

        verifyNoMoreInteractions(loyaltyService);
    }
}
