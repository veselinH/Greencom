package bg.greencom.greencomwebapp.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping()
    public String index() {
        return "index";
    }

    @GetMapping("home")
    public String home() {
        return "home";
    }

    @GetMapping("about")
    public String about() {
        return "about";
    }


}
