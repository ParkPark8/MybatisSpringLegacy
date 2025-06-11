package org.scoula.ex03.controller;

import java.util.Arrays;

import org.scoula.ex03.dto.SampleDTO;
import org.scoula.ex03.dto.SampleDTOList;
import org.scoula.ex03.dto.TodoDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/sample")
@Log4j2
public class SampleController {
	@RequestMapping("") // url : /sample
	public void basic() {
		log.info("basic.......");
	}

	@RequestMapping(value = "/baisc", method = {RequestMethod.GET, RequestMethod.POST})
	public void basicGet() {
		log.info("basic get .............");
	}

	@GetMapping("/basicOnlyGet") //url : /sample/basicOnlyGet
	public void basicGet2() {
		log.info("baisc get only get........");
	}

	@GetMapping("/ex01")
	public String ex01(SampleDTO dto) {
		log.info("" + dto);
		return "ex01";
	}

	@GetMapping("/ex02")
	public String ex02(
		@RequestParam("name") String name,
		@RequestParam("age") int age) {
		log.info("name:" + name);
		log.info("age:" + age);
		return "ex02";
	}
	@GetMapping("/ex02Array")
	public String ex02Array(
		@RequestParam("ids") String[] ids){
		log.info("array ids : "+ Arrays.toString(ids));
		return "ex02Array";
	}
	@GetMapping("/ex02Bean")
	public String ex02Bean(SampleDTOList list){
		log.info("list dtos : " + list);
		return "ex02Bean";
	}
	@GetMapping("/ex03")
	public String ex03(TodoDTO todo){
		log.info("todo : " + todo);
		return "ex03";
	}

	//RedirectAttributes ra 매개변수 지정 가정..
	//addFlashAttribute(이름,값) 메서드로 지정
	@GetMapping("/ex06")
	public String ex06(RedirectAttributes ra){
		log.info("/ex06 .....");
		ra.addAttribute("name","AAA");
		ra.addAttribute("age",10);

		return "redirect:/sample/ex06-2";
	}
	@GetMapping("/ex07")
	public @ResponseBody SampleDTO ex07(){
		log.info("/ex07 ........");

		SampleDTO dto = new SampleDTO();
		dto.setAge(10);
		dto.setName("홍길동");

		return dto;
	}
	@GetMapping("/ex08")
	public ResponseEntity<String> ex08(){
		log.info("/ex08.d.d.d.");
		String msg = "{\"name\": \"홍길동\"}";

		HttpHeaders header = new HttpHeaders();
		header.add("Content-Type","application/json;charset=UTF-8");

		return new ResponseEntity<>(msg,header, HttpStatus.OK);
	}
	@GetMapping("/ex04")
	public String ex04(SampleDTO dto,@ModelAttribute("page") int page){
		log.info("dto : " + dto);
		log.info("page : " + page);
		return "sample/ex04";
	}
}

