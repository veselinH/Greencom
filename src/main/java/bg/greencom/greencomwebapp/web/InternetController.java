package bg.greencom.greencomwebapp.web;

import bg.greencom.greencomwebapp.model.binding.InternetPlanBindingModel;
import bg.greencom.greencomwebapp.model.service.InternetPlanServiceModel;
import bg.greencom.greencomwebapp.service.InternetPlanService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/internet")
public class InternetController {

    private final InternetPlanService internetPlanService;
    private final ModelMapper modelMapper;

    public InternetController(InternetPlanService internetPlanService, ModelMapper modelMapper) {
        this.internetPlanService = internetPlanService;
        this.modelMapper = modelMapper;
    }

    @ModelAttribute
    public InternetPlanBindingModel internetPlanBindingModel() {
        return new InternetPlanBindingModel();
    }

    @GetMapping("/internet-plans")
    public String internetPlans() {
        return "internet-plans/internet-plans";
    }

    @GetMapping("/add-internet-plan")
    public String addVoicePlan() {return "internet-plans/add-internet-plan";}

    @PostMapping("/add-internet-plan")
    public String addVoicePlanConfirm(@Valid InternetPlanBindingModel internetPlanBindingModel,
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

}
