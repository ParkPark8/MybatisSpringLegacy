package org.scoula.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class HomeController {
	@GetMapping("/")
	public String home(Model model){
		model.addAttribute("name","홍길동");

		return "index"; //forward
	}
}
