package bg.greencom.greencomwebapp.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/landline")
public class LandlineController {

    @GetMapping("/landline-plans")
    public String landlinePlans() {
        return "landline-plans";
    }
}
