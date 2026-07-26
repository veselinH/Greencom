package bg.greencom.greencomwebapp.client;

import bg.greencom.greencomwebapp.client.dto.EarnRequest;
import bg.greencom.greencomwebapp.client.dto.LoyaltyResponse;
import bg.greencom.greencomwebapp.client.dto.RedeemRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

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
