package bg.greencom.loyalty.web;

import bg.greencom.loyalty.dto.EarnRequest;
import bg.greencom.loyalty.dto.LoyaltyResponse;
import bg.greencom.loyalty.dto.RedeemRequest;
import bg.greencom.loyalty.service.LoyaltyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loyalty")
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    public LoyaltyController(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<LoyaltyResponse> getAccount(@PathVariable String username) {
        return ResponseEntity.ok(loyaltyService.getAccount(username));
    }

    @PostMapping("/{username}/earn")
    public ResponseEntity<LoyaltyResponse> earn(@PathVariable String username,
                                                @Valid @RequestBody EarnRequest request) {
        return ResponseEntity.ok(loyaltyService.earn(username, request.getPoints()));
    }

    @PutMapping("/{username}/redeem")
    public ResponseEntity<LoyaltyResponse> redeem(@PathVariable String username,
                                                  @Valid @RequestBody RedeemRequest request) {
        return ResponseEntity.ok(loyaltyService.redeem(username, request.getPoints()));
    }

    @DeleteMapping("/{username}/points")
    public ResponseEntity<LoyaltyResponse> revoke(@PathVariable String username,
                                                  @RequestParam int amount) {
        return ResponseEntity.ok(loyaltyService.revoke(username, amount));
    }
}
