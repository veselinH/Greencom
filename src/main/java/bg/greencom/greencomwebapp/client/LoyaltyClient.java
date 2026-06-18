package bg.greencom.greencomwebapp.client;

import bg.greencom.greencomwebapp.client.dto.EarnRequest;
import bg.greencom.greencomwebapp.client.dto.LoyaltyResponse;
import bg.greencom.greencomwebapp.client.dto.RedeemRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Feign client for the standalone loyalty-service (runs on localhost:8081).
 * The base URL is configured statically via {@code loyalty.service.url} since
 * everything runs locally (no service discovery).
 */
@FeignClient(name = "loyalty-service", url = "${loyalty.service.url}", path = "/api/loyalty")
public interface LoyaltyClient {

    @GetMapping("/{username}")
    LoyaltyResponse getBalance(@PathVariable("username") String username);

    @PostMapping("/{username}/earn")
    LoyaltyResponse earn(@PathVariable("username") String username, @RequestBody EarnRequest request);

    @PutMapping("/{username}/redeem")
    LoyaltyResponse redeem(@PathVariable("username") String username, @RequestBody RedeemRequest request);

    @DeleteMapping("/{username}/points")
    LoyaltyResponse revoke(@PathVariable("username") String username, @RequestParam("amount") int amount);
}
