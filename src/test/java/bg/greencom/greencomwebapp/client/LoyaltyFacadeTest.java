package bg.greencom.greencomwebapp.client;

import bg.greencom.greencomwebapp.client.dto.EarnRequest;
import bg.greencom.greencomwebapp.client.dto.LoyaltyResponse;
import bg.greencom.greencomwebapp.client.dto.RedeemRequest;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoyaltyFacadeTest {

    private static final String USERNAME = "ivan";

    @Mock
    private LoyaltyClient loyaltyClient;

    private LoyaltyFacade loyaltyFacade;

    @BeforeEach
    void setUp() {
        loyaltyFacade = new LoyaltyFacade(loyaltyClient);
    }

    @Test
    void getBalance_returnsResponseFromClient() {
        LoyaltyResponse response = new LoyaltyResponse();
        when(loyaltyClient.getBalance(USERNAME)).thenReturn(response);

        assertSame(response, loyaltyFacade.getBalance(USERNAME));
    }

    @Test
    void getBalance_returnsNullWhenClientFails() {
        when(loyaltyClient.getBalance(USERNAME)).thenThrow(new RuntimeException("down"));

        assertNull(loyaltyFacade.getBalance(USERNAME));
    }

    @Test
    void earn_delegatesToClient() {
        loyaltyFacade.earn(USERNAME, 100);

        verify(loyaltyClient).earn(eq(USERNAME), any(EarnRequest.class));
    }

    @Test
    void earn_skipsNonPositivePoints() {
        loyaltyFacade.earn(USERNAME, 0);

        verifyNoInteractions(loyaltyClient);
    }

    @Test
    void earn_swallowsClientFailure() {
        doThrow(new RuntimeException("down")).when(loyaltyClient).earn(anyString(), any(EarnRequest.class));

        assertDoesNotThrow(() -> loyaltyFacade.earn(USERNAME, 100));
    }

    @Test
    void revoke_delegatesToClient() {
        loyaltyFacade.revoke(USERNAME, 50);

        verify(loyaltyClient).revoke(USERNAME, 50);
    }

    @Test
    void revoke_skipsNonPositiveAmount() {
        loyaltyFacade.revoke(USERNAME, -5);

        verifyNoInteractions(loyaltyClient);
    }

    @Test
    void revoke_swallowsClientFailure() {
        doThrow(new RuntimeException("down")).when(loyaltyClient).revoke(anyString(), anyInt());

        assertDoesNotThrow(() -> loyaltyFacade.revoke(USERNAME, 50));
    }

    @Test
    void redeem_returnsResponseFromClient() {
        LoyaltyResponse response = new LoyaltyResponse();
        when(loyaltyClient.redeem(eq(USERNAME), any(RedeemRequest.class))).thenReturn(response);

        assertSame(response, loyaltyFacade.redeem(USERNAME, 100));
    }

    @Test
    void redeem_translatesBadRequestToInsufficientPointsMessage() {
        when(loyaltyClient.redeem(eq(USERNAME), any(RedeemRequest.class)))
                .thenThrow(mock(FeignException.BadRequest.class));

        LoyaltyException exception = assertThrows(LoyaltyException.class,
                () -> loyaltyFacade.redeem(USERNAME, 100));

        assertTrue(exception.getMessage().contains("enough points"));
    }

    @Test
    void redeem_translatesOtherFailuresToServiceUnavailableMessage() {
        when(loyaltyClient.redeem(eq(USERNAME), any(RedeemRequest.class)))
                .thenThrow(new RuntimeException("down"));

        LoyaltyException exception = assertThrows(LoyaltyException.class,
                () -> loyaltyFacade.redeem(USERNAME, 100));

        assertTrue(exception.getMessage().contains("unavailable"));
    }
}
