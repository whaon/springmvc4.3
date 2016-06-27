package com.my.web;

import java.util.Date;
import java.util.Map;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.my.entity.Dog;

//@RestController()
@Controller
@RequestMapping("/rest")
public class Rest {

	@RequestMapping("/test")
	@ResponseBody
	public ResponseEntity<Dog> test(@RequestParam(required=false) String var, Dog dog) {
		System.out.println(var);
		System.out.println(dog);
		Dog d = new Dog();
		d.setBirth(new Date());
		d.setName("doggy");
		
		//ResponseEntity<Dog> r = new ResponseEntity<Dog>(d, HttpStatus.MOVED_PERMANENTLY);
		ResponseEntity<Dog> r = ResponseEntity.accepted().body(d);
		return r;
	}
	
	@RequestMapping("/request")
	@ResponseBody
	public Dog json(RequestEntity<Dog> e, WebRequest req) {
		
		System.out.println(e);
		
		return new Dog();
	}
	
	@GetMapping("/request2")
	@ResponseBody
	public Dog json(@RequestHeader Map head, @RequestBody(required=false) Object o) {
		
		System.out.println(head);
		System.out.println(o);
		
		//WebApplicationContextUtils.
		
		return new Dog();
	}
	
	@RequestMapping("/myjson")
	@ResponseBody
	public ModelAndView myjson(Dog dog) {
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
	
	@GetMapping("/path/{name}")
	public String get(@PathVariable String name, Dog arg, Model model) {
		System.out.println(name);
		return "myself";
	}

}
