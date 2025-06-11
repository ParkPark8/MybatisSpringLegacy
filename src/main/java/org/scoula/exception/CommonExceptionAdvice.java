package org.scoula.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class CommonExceptionAdvice {
	@ExceptionHandler(Exception.class)
	public String except(Exception ex, Model model){

		log.error("Exception ...." + ex.getMessage());
		model.addAttribute("exception",ex);
		log.error(model);
		return "error_page";
	}
	// @ExceptionHandler(NoHandlerFoundException.class)
	// @ResponseStatus(HttpStatus.NOT_FOUND)
	// public String handle404(NoHandlerFoundException ex) {
	// 	log.error(ex);
	// 	model.addAttribute("uri", request.getRequestURI());
	// 	return "custom404";
	// }
}
