package bg.greencom.greencomwebapp.web;

import bg.greencom.greencomwebapp.model.binding.InternetPlanBindingModel;
import bg.greencom.greencomwebapp.model.service.InternetPlanServiceModel;
import bg.greencom.greencomwebapp.model.user.GreencomUserDetails;
import bg.greencom.greencomwebapp.model.view.InternetPlanViewModel;
import bg.greencom.greencomwebapp.model.view.VoicePlanViewModel;
import bg.greencom.greencomwebapp.service.InternetPlanService;
import bg.greencom.greencomwebapp.service.UserService;
import bg.greencom.greencomwebapp.validation.group.onCreate;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Base64;

@Controller
@RequestMapping("/internet")
public class InternetController {

    private final InternetPlanService internetPlanService;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public InternetController(InternetPlanService internetPlanService, ModelMapper modelMapper, UserService userService) {
        this.internetPlanService = internetPlanService;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    @ModelAttribute
    public InternetPlanBindingModel internetPlanBindingModel() {
        return new InternetPlanBindingModel();
    }

    @GetMapping("/internet-plans")
    public String internetPlans(Model model) {

        model.addAttribute("allInternetPlans", internetPlanService.findAllPlansOrderedByPrice());

        return "internet-plans/internet-plans";
    }

    @GetMapping("/add-internet-plan")
    @PreAuthorize("hasRole('ADMIN')")
    public String addVoicePlan() {return "internet-plans/add-internet-plan";}

    @PostMapping("/add-internet-plan")
    @PreAuthorize("hasRole('ADMIN')")
    public String addVoicePlanConfirm(@Validated(onCreate.class) InternetPlanBindingModel internetPlanBindingModel,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()){
            redirectAttributes
                    .addFlashAttribute("internetPlanBindingModel", internetPlanBindingModel)
                    .addFlashAttribute("org.springframework.validation.BindingResult.internetPlanBindingModel", bindingResult);

            return "redirect:/internet/add-internet-plan";
        }

        internetPlanService.addPlan(modelMapper.map(internetPlanBindingModel, InternetPlanServiceModel.class));

        return "redirect:/internet/internet-plans";
    }

    @GetMapping("/edit-internet-plan/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editInternetPlan(@PathVariable Long id, Model model) {
        InternetPlanViewModel internetPlanFromRepo = modelMapper.map(internetPlanService.findById(id), InternetPlanViewModel.class);

        model.addAttribute("internetPlanFromRepo", internetPlanFromRepo);

        return "internet-plans/edit-internet-plan";
    }

    @GetMapping("/edit-internet-plan/{id}/errors")
    public String editInternetPlanError(@PathVariable Long id){

        return "internet-plans/edit-internet-plan";
    }

    @PatchMapping("/edit-internet-plan/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editInternetPlanConfirm(@PathVariable Long id,
                                          @Valid InternetPlanBindingModel internetPlanBindingModel,
                                          BindingResult bindingResult,
                                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()){

            redirectAttributes
                    .addFlashAttribute("internetPlanFromRepo", internetPlanBindingModel)
                    .addFlashAttribute("org.springframework.validation.BindingResult.internetPlanFromRepo", bindingResult);

            return "redirect:/internet/edit-internet-plan/" + id + "/errors";
        }

        internetPlanService.updateInternetPlan(modelMapper.map(internetPlanBindingModel, InternetPlanServiceModel.class));

        return "redirect:/internet/internet-plans";
    }

    @GetMapping("/internet-plan/{id}")
    public String signInternetPlan(@PathVariable Long id, Model model){
        InternetPlanViewModel internetPlanViewModel = internetPlanService.findById(id);

        model.addAttribute("internetPlanFromRepo", internetPlanViewModel);

        return "internet-plans/internet-plans";
    }

//    Sign a contract to the user
    @PatchMapping("/internet-plan/{id}")
    public String signInternetPlanConfirm(@PathVariable Long id,
                                          @AuthenticationPrincipal GreencomUserDetails userDetails,
                                          @RequestParam String signature) {

//        Decode the signature image
        String base64Data = signature.split(",")[1];
        byte[] signSignature = Base64.getDecoder().decode(base64Data);
//        Retrieve the internet plan
        InternetPlanViewModel internetPlan = internetPlanService.findById(id);

        userService.signInternetPlan(internetPlan, userDetails, signSignature);

        return "redirect:/internet/internet-plans";
    }

}
