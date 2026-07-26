package bg.greencom.greencomwebapp.util;

import bg.greencom.greencomwebapp.repository.ContractRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MobileNumberGeneratorTest {

    @Mock
    private ContractRepository contractRepository;

    @Test
    void generate_returnsNumberStartingWith083FollowedBySevenDigits() {
        MobileNumberGenerator generator = new MobileNumberGenerator(contractRepository);
        when(contractRepository.existsByMobileNumber(anyString())).thenReturn(false);

        for (int i = 0; i < 500; i++) {
            String number = generator.generate();

            assertEquals(10, number.length());
            assertTrue(number.startsWith("083"), "Expected 083 prefix but got " + number);
            assertTrue(number.matches("^083\\d{7}$"), "Malformed mobile number " + number);
        }
    }

    @Test
    void generate_retriesWhenNumberIsAlreadyTaken() {
        MobileNumberGenerator generator = new MobileNumberGenerator(contractRepository);
        when(contractRepository.existsByMobileNumber(anyString()))
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);

        String number = generator.generate();

        assertTrue(number.matches("^083\\d{7}$"));
        verify(contractRepository, times(3)).existsByMobileNumber(anyString());
    }

    @Test
    void generate_throwsWhenEveryCandidateIsTaken() {
        MobileNumberGenerator generator = new MobileNumberGenerator(contractRepository);
        when(contractRepository.existsByMobileNumber(anyString())).thenReturn(true);

        assertThrows(IllegalStateException.class, generator::generate);
        verify(contractRepository, times(10)).existsByMobileNumber(anyString());
    }
}
