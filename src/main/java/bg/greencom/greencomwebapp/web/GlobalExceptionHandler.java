package bg.greencom.greencomwebapp.web;

import bg.greencom.greencomwebapp.model.exception.ContractAccessDeniedException;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ObjectNotFoundException.class)
    public ModelAndView handleObjectNotFound(ObjectNotFoundException exception) {
        LOGGER.warn("Requested entity was not found: {}", exception.getMessage());

        ModelAndView modelAndView = new ModelAndView("error/404");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);

        return modelAndView;
    }

    @ExceptionHandler(ContractAccessDeniedException.class)
    public ModelAndView handleContractAccessDenied(ContractAccessDeniedException exception) {
        LOGGER.warn("Contract access denied: {}", exception.getMessage());

        ModelAndView modelAndView = new ModelAndView("error/403");
        modelAndView.setStatus(HttpStatus.FORBIDDEN);

        return modelAndView;
    }
}
