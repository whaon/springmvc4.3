package com.my.web;

import java.util.Date;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.my.entity.Dog;

@Controller
@RequestMapping("/api")
//@RestController("/api")
public class Test {

	@RequestMapping("/test")
	@ResponseBody
	public ResponseEntity<Dog> test(@RequestParam(required=false) Dog dog) {
		System.out.println(dog);
		Dog d = new Dog();
		d.setBirth(new  Date());
		d.setName("doggy");
		
		ResponseEntity<Dog> r = new ResponseEntity<Dog>(d, HttpStatus.MOVED_PERMANENTLY);
		return r;
	}
	
	@RequestMapping("/testjson")
	@ResponseBody
	public Dog json(Dog dog) {
		System.out.println(dog);
		Dog d = new Dog();
		d.setBirth(new  Date());
		d.setName("doggy");
		
		return d;
	}
	
	@RequestMapping("/myjson")
	@ResponseBody
	public ModelAndView myjson(@RequestParam(required=false) Dog dog) {
		System.out.println(dog);
		Dog d = new Dog();
		d.setBirth(new  Date());
		d.setName("doggyfffffffff");
		
		ModelAndView m = new ModelAndView();
		m.setViewName("myjson");
		m.addObject("json", d);
		return m;
	}
	
	@RequestMapping("/test2")
	public String test2(Map m) {
		System.out.println(m);
		return "myself";
	}
	
	@RequestMapping("/test3")
	public String test3(Dog arg) {
		return "yourself";
	}
	
	@GetMapping("/get")
	public String get(Dog arg) {
		return "myself";
	}

}
