package bg.greencom.greencomwebapp.web;

import bg.greencom.greencomwebapp.model.binding.PlanSignBindingModel;
import bg.greencom.greencomwebapp.model.binding.VoicePlanBindingModel;
import bg.greencom.greencomwebapp.model.service.VoicePlanServiceModel;
import bg.greencom.greencomwebapp.model.user.GreencomUserDetails;
import bg.greencom.greencomwebapp.model.view.VoicePlanViewModel;
import bg.greencom.greencomwebapp.service.UserService;
import bg.greencom.greencomwebapp.service.VoicePlanService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MobileController {

    private final VoicePlanService voicePlanService;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public MobileController(VoicePlanService voicePlanService, ModelMapper modelMapper, UserService userService) {
        this.voicePlanService = voicePlanService;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    @ModelAttribute
    public VoicePlanBindingModel voicePlanBindingModel() {
        return new VoicePlanBindingModel();
    }


    @GetMapping("/voice-mobile-plans")
    public String voicePlans(Model model) {

        model.addAttribute("allVoicePlans", voicePlanService.findAllPlansOrderedByPrice());

        return "voice-mobile-plans";
    }

    @GetMapping("/data-mobile-plans")
    public String dataPlans() {
        return "data-mobile-plans";
    }

    @GetMapping("/add-voice-mobile-plan")
    public String addVoicePlan() {
        return "add-voice-mobile-plan";
    }

    @PostMapping("/add-voice-mobile-plan")
    public String addVoicePlanConfirm(@Valid VoicePlanBindingModel voicePlanBindingModel,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes
                    .addFlashAttribute("voicePlanBindingModel", voicePlanBindingModel)
                    .addFlashAttribute("org.springframework.validation.BindingResult.voicePlanBindingModel", bindingResult);

            return "redirect:add-voice-mobile-plan";
        }


        VoicePlanServiceModel voicePlanServiceModel = modelMapper.map(voicePlanBindingModel, VoicePlanServiceModel.class);
        voicePlanService.addPlan(voicePlanServiceModel);

        return "redirect:voice-mobile-plans";
    }


    @DeleteMapping("/voice-mobile-plans/{name}")
    public String removeVoicePlan(@PathVariable String name) {

        voicePlanService.deleteVoicePlan(name);

        return "redirect:/voice-mobile-plans";
    }

    @GetMapping("/edit-voice-mobile-plan/{id}")
    public String editVoicePlan(@PathVariable Long id, Model model) {
        VoicePlanViewModel voicePlanById = voicePlanService.findById(id);

        model.addAttribute("voicePlanFromRepo", voicePlanById);

        return "edit-voice-mobile-plan";
    }


    @PatchMapping("/edit-voice-mobile-plan/{id}")
    public String editVoicePlanConfirm(@PathVariable Long id,
                                       @Valid VoicePlanBindingModel voicePlanBindingModel,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            redirectAttributes
                    .addFlashAttribute("voicePlanBindingModel", voicePlanBindingModel)
                    .addFlashAttribute("org.springframework.validation.BindingResult.voicePlanBindingModel", bindingResult);

            return "redirect:edit-voice-mobile-plan/" + id;
        }

        voicePlanService.updatePlan(modelMapper.map(voicePlanBindingModel, VoicePlanServiceModel.class));

        return "redirect:/voice-mobile-plans";
    }

    @GetMapping("/voice-mobile-plans/{id}")
    public String signVoicePlan(@PathVariable Long id, Model model) {
        VoicePlanViewModel voicePlanById = voicePlanService.findById(id);

        model.addAttribute("voicePlanFromRepo", voicePlanById);

        return "voice-mobile-plans";
    }

    @PatchMapping("/voice-mobile-plans/{id}")
    public String signVoicePlanConfirm(@PathVariable Long id,
                           PlanSignBindingModel planSignBindingModel,
                           @AuthenticationPrincipal GreencomUserDetails userDetails) {


        VoicePlanViewModel voicePlan = voicePlanService.findById(id);

        userService.addVoicePlan(voicePlan, userDetails);

        return "redirect:/voice-mobile-plans";
    }
}
