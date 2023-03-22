package bg.greencom.greencomwebapp.web;

import bg.greencom.greencomwebapp.model.exception.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ObjectNotFoundAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ObjectNotFoundException.class)
    public ModelAndView onPlanNotFound(ObjectNotFoundException exception) {

        ModelAndView modelAndView = new ModelAndView("error/custom-errors/object-not-found");
        modelAndView.addObject("objectId", exception.getObjectId());
        modelAndView.addObject("objectType", exception.getObjectType());
        return modelAndView;
    }
}
