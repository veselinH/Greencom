package bg.greencom.greencomwebapp.web;

import bg.greencom.greencomwebapp.model.binding.TelevisionPlanBindingModel;
import bg.greencom.greencomwebapp.service.TelevisionPlanService;
import bg.greencom.greencomwebapp.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/television")
public class TelevisionController {

    private final TelevisionPlanService televisionPlanService;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public TelevisionController(TelevisionPlanService televisionPlanService, ModelMapper modelMapper, UserService userService) {
        this.televisionPlanService = televisionPlanService;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    @ModelAttribute
    public TelevisionPlanBindingModel televisionPlanBindingModel(){
        return new TelevisionPlanBindingModel();
    }

    @GetMapping("/television-plans")
    public String televisionPlans(Model model){

        model.addAttribute("allTelevisionPlans", televisionPlanService.findAllPlansOrderedByPrice());

        return "television-plans/television-plans";
    }

    @GetMapping("/add-television-plan")
    @PreAuthorize("hasRole('ADMIN')")
    public String addTelevisionPlan() {return "television-plans/add-television-plan";}



}
