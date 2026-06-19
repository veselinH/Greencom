package bg.greencom.greencomwebapp.web;

import bg.greencom.greencomwebapp.model.exception.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * Global handler for {@link ObjectNotFoundException}.
 * Renders the custom object-not-found error page with the missing entity's type and ID
 * instead of the generic 404, so users see a more descriptive message.
 */
@ControllerAdvice
public class ObjectNotFoundAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ObjectNotFoundException.class)
    public ModelAndView onObjectNotFound(ObjectNotFoundException exception) {

        ModelAndView modelAndView = new ModelAndView("error/custom-errors/object-not-found");
        modelAndView.addObject("objectId", exception.getObjectId());
        modelAndView.addObject("objectType", exception.getObjectType());
        return modelAndView;
    }
}
