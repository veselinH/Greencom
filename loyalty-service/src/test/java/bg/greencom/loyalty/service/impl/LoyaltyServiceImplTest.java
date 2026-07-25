package bg.greencom.loyalty.service.impl;

import bg.greencom.loyalty.dto.LoyaltyResponse;
import bg.greencom.loyalty.model.LoyaltyAccount;
import bg.greencom.loyalty.repository.LoyaltyAccountRepository;
import bg.greencom.loyalty.service.InsufficientPointsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoyaltyServiceImplTest {

    private static final String USERNAME = "ivan";

    @Mock
    private LoyaltyAccountRepository loyaltyAccountRepository;

    private LoyaltyServiceImpl loyaltyService;

    @BeforeEach
    void setUp() {
        loyaltyService = new LoyaltyServiceImpl(loyaltyAccountRepository);
    }

    private LoyaltyAccount account(int balance, int totalEarned) {
        return new LoyaltyAccount()
                .setId(1L)
                .setUsername(USERNAME)
                .setPointsBalance(balance)
                .setTotalEarned(totalEarned)
                .setUpdatedOn(LocalDateTime.now());
    }

    private void mockExistingAccount(LoyaltyAccount account) {
        when(loyaltyAccountRepository.findByUsername(USERNAME))
                .thenReturn(Optional.of(account));
    }

    private void mockSaveAndFlushReturnsArgument() {
        when(loyaltyAccountRepository.saveAndFlush(any(LoyaltyAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void getAccount_returnsExistingAccount() {
        mockExistingAccount(account(250, 250));

        LoyaltyResponse response = loyaltyService.getAccount(USERNAME);

        assertThat(response.getUsername()).isEqualTo(USERNAME);
        assertThat(response.getPointsBalance()).isEqualTo(250);
        assertThat(response.getTier()).isEqualTo("BRONZE");
        assertThat(response.getDiscountBgn()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(loyaltyAccountRepository, never()).saveAndFlush(any());
    }

    @Test
    void getAccount_createsAccountWhenMissing() {
        when(loyaltyAccountRepository.findByUsername(USERNAME))
                .thenReturn(Optional.empty());
        mockSaveAndFlushReturnsArgument();

        LoyaltyResponse response = loyaltyService.getAccount(USERNAME);

        assertThat(response.getUsername()).isEqualTo(USERNAME);
        assertThat(response.getPointsBalance()).isZero();
        assertThat(response.getTotalEarned()).isZero();
        assertThat(response.getTier()).isEqualTo("BRONZE");

        ArgumentCaptor<LoyaltyAccount> captor = ArgumentCaptor.forClass(LoyaltyAccount.class);
        verify(loyaltyAccountRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo(USERNAME);
    }

    @Test
    void earn_increasesBalanceAndTotalEarned() {
        mockExistingAccount(account(100, 400));
        mockSaveAndFlushReturnsArgument();

        LoyaltyResponse response = loyaltyService.earn(USERNAME, 150);

        assertThat(response.getPointsBalance()).isEqualTo(250);
        assertThat(response.getTotalEarned()).isEqualTo(550);
        assertThat(response.getTier()).isEqualTo("SILVER");
    }

    @Test
    void redeem_decreasesBalanceAndCalculatesDiscount() {
        mockExistingAccount(account(500, 500));
        mockSaveAndFlushReturnsArgument();

        LoyaltyResponse response = loyaltyService.redeem(USERNAME, 200);

        assertThat(response.getPointsBalance()).isEqualTo(300);
        assertThat(response.getTotalEarned()).isEqualTo(500);
        assertThat(response.getDiscountBgn()).isEqualByComparingTo(new BigDecimal("2"));
    }

    @Test
    void redeem_throwsWhenBalanceIsInsufficient() {
        mockExistingAccount(account(100, 100));

        assertThatExceptionOfType(InsufficientPointsException.class)
                .isThrownBy(() -> loyaltyService.redeem(USERNAME, 200))
                .withMessageContaining("200")
                .withMessageContaining("100");

        verify(loyaltyAccountRepository, never()).saveAndFlush(any());
    }

    @Test
    void revoke_decreasesBalance() {
        mockExistingAccount(account(300, 300));
        mockSaveAndFlushReturnsArgument();

        LoyaltyResponse response = loyaltyService.revoke(USERNAME, 100);

        assertThat(response.getPointsBalance()).isEqualTo(200);
        assertThat(response.getTotalEarned()).isEqualTo(300);
    }

    @Test
    void revoke_clampsBalanceAtZero() {
        mockExistingAccount(account(50, 50));
        mockSaveAndFlushReturnsArgument();

        LoyaltyResponse response = loyaltyService.revoke(USERNAME, 100);

        assertThat(response.getPointsBalance()).isZero();
    }

    @ParameterizedTest
    @CsvSource({
            "0, BRONZE",
            "499, BRONZE",
            "500, SILVER",
            "1499, SILVER",
            "1500, GOLD"
    })
    void getAccount_resolvesTierFromTotalEarned(int totalEarned, String expectedTier) {
        mockExistingAccount(account(0, totalEarned));

        LoyaltyResponse response = loyaltyService.getAccount(USERNAME);

        assertThat(response.getTier()).isEqualTo(expectedTier);
    }

    @ParameterizedTest
    @CsvSource({
            "499, 10",
            "500, 50",
            "1499, 50",
            "1500, 100"
    })
    void awardTierBonusToAll_awardsBonusByTier(int totalEarned, int expectedBonus) {
        LoyaltyAccount account = account(100, totalEarned);
        when(loyaltyAccountRepository.findAll()).thenReturn(List.of(account));

        int affected = loyaltyService.awardTierBonusToAll();

        assertThat(affected).isEqualTo(1);
        assertThat(account.getPointsBalance()).isEqualTo(100 + expectedBonus);
        assertThat(account.getTotalEarned()).isEqualTo(totalEarned + expectedBonus);
        verify(loyaltyAccountRepository).saveAll(List.of(account));
    }

    @Test
    void awardTierBonusToAll_returnsZeroWhenNoAccountsExist() {
        when(loyaltyAccountRepository.findAll()).thenReturn(List.of());

        assertThat(loyaltyService.awardTierBonusToAll()).isZero();
    }
}
