package bg.greencom.greencomwebapp.util;

import bg.greencom.greencomwebapp.repository.ContractRepository;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

@Component
public class MobileNumberGenerator {

    private static final String PREFIX = "083";
    private static final int SUBSCRIBER_DIGITS = 7;
    private static final int UPPER_BOUND = 10_000_000;
    private static final int MAX_ATTEMPTS = 10;

    private final ContractRepository contractRepository;
    private final Random random = new SecureRandom();

    public MobileNumberGenerator(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    public String generate() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String candidate = PREFIX + String.format("%0" + SUBSCRIBER_DIGITS + "d", random.nextInt(UPPER_BOUND));
            if (!contractRepository.existsByMobileNumber(candidate)) {
                return candidate;
            }
        }

        throw new IllegalStateException(
                "Could not generate a free mobile number after " + MAX_ATTEMPTS + " attempts.");
    }
}
