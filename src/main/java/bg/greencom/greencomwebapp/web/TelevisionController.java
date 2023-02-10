package bg.greencom.greencomwebapp.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/television")
public class TelevisionController {

    @GetMapping("/television-plans")
    public String televisionPlans(){
        return "television-plans";
    }
}
