package bg.greencom.greencomwebapp.web;

import bg.greencom.greencomwebapp.model.binding.UserRegisterBindingModel;
import bg.greencom.greencomwebapp.model.service.UserServiceModel;
import bg.greencom.greencomwebapp.model.user.GreencomUserDetails;
import bg.greencom.greencomwebapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final SecurityContextRepository securityContextRepository;

    public UserController(UserService userService, ModelMapper modelMapper, SecurityContextRepository securityContextRepository) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.securityContextRepository = securityContextRepository;
    }

    @ModelAttribute
    public UserRegisterBindingModel userRegisterBindingModel() {
        return new UserRegisterBindingModel();
    }

    @GetMapping("/login")
    public String login(@AuthenticationPrincipal GreencomUserDetails user) {
        if (user != null) {
            return "redirect:/home";
        }
        return "login";
    }

    @PostMapping("/login-errors")
    public String onFailedLogin(
            @ModelAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY) String username,
            RedirectAttributes redirectAttributes) {

        redirectAttributes
                .addFlashAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY, username)
                .addFlashAttribute("bad_credentials", true);

        return "redirect:/users/login";
    }

    @GetMapping("/register")
    public String register(@AuthenticationPrincipal GreencomUserDetails user) {
        if (user != null) {
            return "redirect:/home";
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid UserRegisterBindingModel userRegisterBindingModel,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest request,
                               HttpServletResponse response) {

//      Control on the password
        if (!userRegisterBindingModel.getPassword().equals(userRegisterBindingModel.getConfirmPassword())) {
            bindingResult.addError(
                    new FieldError(
                            "differentConfirmPassword",
                            "confirmPassword",
                            "Passwords must be the same."));
        }

//      If bindingResult return errors from the bindingModel we redirect to the same page with the errors on the fields
        if (bindingResult.hasErrors()) {
            redirectAttributes
                    .addFlashAttribute("userRegisterBindingModel", userRegisterBindingModel)
                    .addFlashAttribute("org.springframework.validation.BindingResult.userRegisterBindingModel", bindingResult);

            return "redirect:register";
        }


        userService.registerUser(modelMapper.map(userRegisterBindingModel, UserServiceModel.class), successfulAuth -> {
//          Auto login after registering an account
            SecurityContextHolderStrategy strategy = SecurityContextHolder.getContextHolderStrategy();

            SecurityContext context = strategy.createEmptyContext();
            context.setAuthentication(successfulAuth);

            strategy.setContext(context);

            securityContextRepository.saveContext(context, request, response);

        });

        return "redirect:/home";
    }

    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal GreencomUserDetails user, Model model) {

        model.addAttribute("userVoicePlans", userService.getAllVoicePlans(user.getUsername()));

        return "profile";
    }

    @DeleteMapping("/unsign/voice/{id}")
    public String unsignVoicePlan (@PathVariable Long id,
                                   @AuthenticationPrincipal GreencomUserDetails user,
                                   RedirectAttributes redirectAttributes) {

        String planNameToRemove = userService.unsignVoicePlan(id, user.getUsername());

        if (!planNameToRemove.isEmpty()){
            redirectAttributes.addFlashAttribute("successMessage", "Plan '" + planNameToRemove + "' successfully removed.");
        }

        return "redirect:/users/profile";
    }
}
