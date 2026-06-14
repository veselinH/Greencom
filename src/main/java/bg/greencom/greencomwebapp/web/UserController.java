package bg.greencom.greencomwebapp.web;

import bg.greencom.greencomwebapp.model.binding.UserRegisterBindingModel;
import bg.greencom.greencomwebapp.model.entity.ContractEntity;
import bg.greencom.greencomwebapp.model.service.UserServiceModel;
import bg.greencom.greencomwebapp.model.user.GreencomUserDetails;
import bg.greencom.greencomwebapp.model.view.ContractViewModel;
import bg.greencom.greencomwebapp.service.ContractService;
import bg.greencom.greencomwebapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Base64;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final SecurityContextRepository securityContextRepository;
    private final ContractService contractService;

    public UserController(UserService userService, ModelMapper modelMapper, SecurityContextRepository securityContextRepository, ContractService contractService) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.securityContextRepository = securityContextRepository;
        this.contractService = contractService;
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

        model
                .addAttribute("userVoicePlans", userService.getAllVoicePlans(user.getUsername()))
                .addAttribute("userDataPlans", userService.getAllDataPlans(user.getUsername()))
                .addAttribute("userInternetPlans", userService.getAllInternetPlans(user.getUsername()))
                .addAttribute("userTelevisionPlans", userService.getAllTelevisionPlans(user.getUsername()))
                .addAttribute("currentUser", userService.getUserInfo(user.getUsername()));

        return "profile";
    }

    @PatchMapping("/unsign/plan/{id}")
    public String unsignPlan (@PathVariable Long id,
                                   @AuthenticationPrincipal GreencomUserDetails user,
                                   @RequestParam String signature,
                                   RedirectAttributes redirectAttributes) {

        if (userService.isPenaltyRequired(id)){
            BigDecimal penaltyAmount = userService.calculatePenalty(id);

            redirectAttributes
                    .addFlashAttribute("showPenalty", true)
                    .addFlashAttribute("penaltyAmount", penaltyAmount)
                    .addFlashAttribute("penaltyContractId", id)
                    .addFlashAttribute("tempSignature", signature);

            return "redirect:/users/profile";
        }

        executeUnsign(id, user.getUsername(), signature, redirectAttributes);
        return "redirect:/users/profile";
    }

    @PatchMapping("/confirm-unsign")
    public String confirmUnsign(@RequestParam Long contractId,
                                @RequestParam String signature,
                                @RequestParam(required = false) String cardNumber,
                                @RequestParam(required = false) String cardCVC,
                                @AuthenticationPrincipal GreencomUserDetails user,
                                RedirectAttributes redirectAttributes) {

        String cleanCard = cardNumber.replaceAll("\\s+", "");
        if (cleanCard.length() != 16 || cardCVC.length() != 3) {
            redirectAttributes.addFlashAttribute("error", "Invalid payment details.");
            return "redirect:/users/profile";
        }

        executeUnsign(contractId, user.getUsername(), signature, redirectAttributes);
        return "redirect:/users/profile";
    }

    @GetMapping("/contract/{id}/download")
    public ResponseEntity<byte[]> downloadContract(@PathVariable Long id) throws Exception {
//        ContractViewModel contract = contractService.findById(id);
        byte[] pdfContents = contractService.generateContractPdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "contract_" + id + ".pdf");

        return new ResponseEntity<>(pdfContents, headers, HttpStatus.OK);
    }

    private void executeUnsign(Long id, String username, String signature, RedirectAttributes redirectAttributes) {
        //        Decoding the signature image
        String base64Data = signature.split(",")[1];
        byte[] unsignSignature = Base64.getDecoder().decode(base64Data);

//        Unsign contract
        String planName = userService.unsignPlan(id, username, unsignSignature);

//        Return successful message on the page
        if (!planName.isEmpty()){
            redirectAttributes.addFlashAttribute("successMessage", "Plan '" + planName + "' successfully unsigned.");
        }


    }

}
