package bg.greencom.greencomwebapp.web;

import bg.greencom.greencomwebapp.model.binding.VoicePlanBindingModel;
import bg.greencom.greencomwebapp.model.service.VoicePlanServiceModel;
import bg.greencom.greencomwebapp.service.VoicePlanService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MobileController {

    private final VoicePlanService voicePlanService;
    private final ModelMapper modelMapper;

    public MobileController(VoicePlanService voicePlanService, ModelMapper modelMapper) {
        this.voicePlanService = voicePlanService;
        this.modelMapper = modelMapper;
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

    @GetMapping("/voice-mobile-plans/{name}")
    public String voicePlanDetail(@PathVariable String name) {

        return "voice-mobile-plans";
    }

    @DeleteMapping("/voice-mobile-plans/{name}")
    public String removeVoicePlan(@PathVariable String name) {

        voicePlanService.deleteVoicePlan(name);

        return "redirect:/voice-mobile-plans";
    }

    @GetMapping("/edit-voice-mobile-plan/{name}")
    public String editVoicePlan(@PathVariable String name) {


        return "edit-voice-mobile-plan";
    }
}
