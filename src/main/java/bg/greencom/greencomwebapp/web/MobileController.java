package bg.greencom.greencomwebapp.web;

import bg.greencom.greencomwebapp.model.binding.DataPlanBindingModel;
import bg.greencom.greencomwebapp.model.binding.PlanSignBindingModel;
import bg.greencom.greencomwebapp.model.binding.VoicePlanBindingModel;
import bg.greencom.greencomwebapp.model.exception.PlanNotFoundException;
import bg.greencom.greencomwebapp.model.service.DataPlanServiceModel;
import bg.greencom.greencomwebapp.model.service.VoicePlanServiceModel;
import bg.greencom.greencomwebapp.model.user.GreencomUserDetails;
import bg.greencom.greencomwebapp.model.view.DataPlanViewModel;
import bg.greencom.greencomwebapp.model.view.VoicePlanViewModel;
import bg.greencom.greencomwebapp.service.DataPlanService;
import bg.greencom.greencomwebapp.service.UserService;
import bg.greencom.greencomwebapp.service.VoicePlanService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mobile")
public class MobileController {

    private final VoicePlanService voicePlanService;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final DataPlanService dataPlanService;

    public MobileController(VoicePlanService voicePlanService, ModelMapper modelMapper, UserService userService, DataPlanService dataPlanService) {
        this.voicePlanService = voicePlanService;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.dataPlanService = dataPlanService;
    }

    // Voice plan section
    // ------------------
    @ModelAttribute
    public VoicePlanBindingModel voicePlanBindingModel() {
        return new VoicePlanBindingModel();
    }



    @GetMapping("/voice-plans")
    public String voicePlans(Model model) {

        model.addAttribute("allVoicePlans", voicePlanService.findAllPlansOrderedByPrice());

        return "mobile-plans/voice-plans/voice-mobile-plans";
    }

    @GetMapping("/add-voice-plan")
    public String addVoicePlan() {
        return "mobile-plans/voice-plans/add-voice-mobile-plan";
    }

    @PostMapping("/add-voice-plan")
    public String addVoicePlanConfirm(@Valid VoicePlanBindingModel voicePlanBindingModel,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes
                    .addFlashAttribute("voicePlanBindingModel", voicePlanBindingModel)
                    .addFlashAttribute("org.springframework.validation.BindingResult.voicePlanBindingModel", bindingResult);

            return "redirect:/mobile/add-voice-plan";
        }


        VoicePlanServiceModel voicePlanServiceModel = modelMapper.map(voicePlanBindingModel, VoicePlanServiceModel.class);
        voicePlanService.addPlan(voicePlanServiceModel);

        return "redirect:/mobile/voice-plans";
    }


    @DeleteMapping("/voice-plan/{name}")
    public String removeVoicePlan(@PathVariable String name) {

        voicePlanService.deleteVoicePlan(name);

        return "redirect:/mobile/voice-plans";
    }

    @GetMapping("/edit-voice-plan/{id}")
    public String editVoicePlan(@PathVariable Long id, Model model) {
        VoicePlanViewModel voicePlanById = voicePlanService.findById(id);
        VoicePlanBindingModel voicePlanFromRepo = modelMapper.map(voicePlanById, VoicePlanBindingModel.class);

        model.addAttribute("voicePlanFromRepo", voicePlanFromRepo);

        return "mobile-plans/voice-plans/edit-voice-mobile-plan";
    }

    @GetMapping("/edit-voice-plan/{id}/errors")
    public String editVoicePlanError(@PathVariable Long id) {

        return "mobile-plans/voice-plans/edit-voice-mobile-plan";
    }

    @PatchMapping("/edit-voice-plan/{id}")
    public String editVoicePlanConfirm(@PathVariable Long id,
                                       @Valid VoicePlanBindingModel voicePlanFromRepo,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            redirectAttributes
                    .addFlashAttribute("voicePlanFromRepo", voicePlanFromRepo)
                    .addFlashAttribute("org.springframework.validation.BindingResult.voicePlanFromRepo", bindingResult);

            return "redirect:/mobile/edit-voice-plan/" + id + "/errors";
        }

        voicePlanService.updatePlan(modelMapper.map(voicePlanFromRepo, VoicePlanServiceModel.class));

        return "redirect:/mobile/voice-plans";
    }

    @GetMapping("/voice-plan/{id}")
    public String signVoicePlan(@PathVariable Long id, Model model) {
        VoicePlanViewModel voicePlanById = voicePlanService.findById(id);

        model.addAttribute("voicePlanFromRepo", voicePlanById);

        return "mobile-plans/voice-plans/voice-mobile-plans";
    }

    @PatchMapping("/voice-plan/{id}")
    public String signVoicePlanConfirm(@PathVariable Long id,
                                       PlanSignBindingModel planSignBindingModel,
                                       @AuthenticationPrincipal GreencomUserDetails userDetails) {


        VoicePlanViewModel voicePlan = voicePlanService.findById(id);

        userService.addVoicePlan(voicePlan, userDetails);

        return "redirect:/mobile/voice-plans";
    }

    // End of voice plan section
    // -------------------------

    // Data plan section
    // -----------------

    @ModelAttribute
    public DataPlanBindingModel dataPlanBindingModel() {
        return new DataPlanBindingModel();
    }

    @GetMapping("/data-plans")
    public String dataPlans(Model model) {

        model.addAttribute("allDataPlans", dataPlanService.findAllPlansOrderedByPrice());

        return "mobile-plans/data-plans/data-mobile-plans";
    }

    @GetMapping("/add-data-plan")
    public String addDataPlan() {
        return "mobile-plans/data-plans/add-data-mobile-plan";
    }

    @PostMapping("/add-data-plan")
    public String addDataPlanConfirm(@Valid DataPlanBindingModel dataPlanBindingModel,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes
                    .addFlashAttribute("dataPlanBindingModel", dataPlanBindingModel)
                    .addFlashAttribute("org.springframework.validation.BindingResult.dataPlanBindingModel", bindingResult);

            return "redirect:/mobile/data-voice-plan";
        }

        DataPlanServiceModel dataPlanServiceModel = modelMapper.map(dataPlanBindingModel, DataPlanServiceModel.class);
        dataPlanService.addPlan(dataPlanServiceModel);

        return "redirect:/mobile/data-plans";
    }

    @DeleteMapping("/data-plan/{name}")
    public String removeDataPlan(@PathVariable String name) {

        dataPlanService.deletePlan(name);

        return "redirect:/mobile/data-plans";
    }

    @GetMapping("/edit-data-plan/{id}")
    public String editDataPlan(@PathVariable Long id, Model model) {
        DataPlanViewModel dataPlanById = dataPlanService.findById(id);
        model.addAttribute("dataPlanFromRepo", dataPlanById);

        return "mobile-plans/data-plans/edit-data-mobile-plan";
    }

    @PatchMapping("/edit-data-plan/{id}")
    public String editDataPlanConfirm(@PathVariable Long id,
                                      @Valid DataPlanViewModel dataPlanBindingModel,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            redirectAttributes
                    .addFlashAttribute("dataPlanBindingModel", dataPlanBindingModel)
                    .addFlashAttribute("org.springframework.validation.BindingResult.dataPlanBindingModel", bindingResult);

            return "redirect:/mobile/edit-data-plan/" + id;
        }

        dataPlanService.updatePlan(modelMapper.map(dataPlanBindingModel, DataPlanServiceModel.class));

        return "redirect:/mobile/data-plans";
    }

    @GetMapping("/data-plan/{id}")
    public String signDataPlan(@PathVariable Long id, Model model) {
        DataPlanViewModel dataPlanById = dataPlanService.findById(id);

        model.addAttribute("dataPlanFromRepo", dataPlanById);

        return "mobile-plans/data-plans/data-mobile-plans";
    }

    @PatchMapping("/data-plan/{id}")
    public String signDataPlanConfirm(@PathVariable Long id,
                                      PlanSignBindingModel planSignBindingModel,
                                      @AuthenticationPrincipal GreencomUserDetails userDetails) {


        DataPlanViewModel dataPlan = dataPlanService.findById(id);

        userService.addDataPlan(dataPlan, userDetails);

        return "redirect:/mobile/data-plans";
    }

    // End of data plan section
    // ------------------------
}
