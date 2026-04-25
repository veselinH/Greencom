package bg.greencom.greencomwebapp.web;

import bg.greencom.greencomwebapp.model.binding.TelevisionPlanBindingModel;
import bg.greencom.greencomwebapp.model.service.TelevisionPlanServiceModel;
import bg.greencom.greencomwebapp.model.user.GreencomUserDetails;
import bg.greencom.greencomwebapp.model.view.TelevisionPlanViewModel;
import bg.greencom.greencomwebapp.service.AdditionalPackageService;
import bg.greencom.greencomwebapp.service.TelevisionPlanService;
import bg.greencom.greencomwebapp.service.UserService;
import bg.greencom.greencomwebapp.validation.group.onCreate;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Base64;
import java.util.Set;

@Controller
@RequestMapping("/television")
public class TelevisionController {

    private final TelevisionPlanService televisionPlanService;
    private final ModelMapper modelMapper;
    private final AdditionalPackageService additionalPackageService;
    private final UserService userService;

    public TelevisionController(TelevisionPlanService televisionPlanService, ModelMapper modelMapper, AdditionalPackageService additionalPackageService, UserService userService) {
        this.televisionPlanService = televisionPlanService;
        this.modelMapper = modelMapper;
        this.additionalPackageService = additionalPackageService;
        this.userService = userService;
    }

    @ModelAttribute
    public TelevisionPlanBindingModel televisionPlanBindingModel(){
        return new TelevisionPlanBindingModel();
    }

    @GetMapping("/television-plans")
    public String televisionPlans(Model model){

        model
                .addAttribute("allTelevisionPlans", televisionPlanService.findAllPlansOrderedByPrice())
                .addAttribute("additionalPackages", additionalPackageService.findAllOrderedByName());

        return "television-plans/television-plans";
    }

    @GetMapping("/add-television-plan")
    @PreAuthorize("hasRole('ADMIN')")
    public String addTelevisionPlan() {return "television-plans/add-television-plan";}

    @PostMapping("/add-television-plan")
    @PreAuthorize("hasRole('ADMIN')")
    public String addTelevisionPlanConfirm(@Validated(onCreate.class) TelevisionPlanBindingModel televisionPlanBindingModel,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()){
            redirectAttributes
                    .addFlashAttribute("televisionPlanBindingModel", televisionPlanBindingModel)
                    .addFlashAttribute("org.springframework.validation.BindingResult.televisionPlanBindingModel", bindingResult);

            return "redirect:/television/add-television-plan";
        }

        televisionPlanService.addPlan(modelMapper.map(televisionPlanBindingModel, TelevisionPlanServiceModel.class));

        return "redirect:/television/television-plans";
    }

//    @GetMapping("/television-plan/{id}")
//    public String signtelevisionPlan(@PathVariable Long id, Model model){
//        TelevisionPlanViewModel televisionPlanViewModel = televisionPlanService.findById(id);
//
//        model.addAttribute("televisionPlanFromRepo", televisionPlanViewModel);
//
//        return "television-plans/television-plans";
//    }

    //    Sign a contract to the user
    @PatchMapping("/television-plan/{id}")
    public String signtelevisionPlanConfirm(@PathVariable Long id,
                                          @RequestParam(name = "selectedExtras", required = false) Set<Long> additionalPackageIds,
                                          @AuthenticationPrincipal GreencomUserDetails userDetails,
                                          @RequestParam String signature) {

//        Decode the signature image
        String base64Data = signature.split(",")[1];
        byte[] signSignature = Base64.getDecoder().decode(base64Data);

        userService.signTelevisionPlan(id, additionalPackageIds, userDetails, signSignature);

        return "redirect:/television/television-plans";
    }


}
