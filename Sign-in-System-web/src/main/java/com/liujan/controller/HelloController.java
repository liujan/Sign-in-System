package com.liujan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/")
public class HelloController {

	@RequestMapping("/")
	public ModelAndView index(ModelAndView modelAndView, HttpServletRequest request) {
		//modelAndView.setViewName("redirect:/teacher/index.html");
		System.out.println("good");
		modelAndView.setViewName("redirect:/student/login.html");
		return modelAndView;
	}
	@RequestMapping("hehe.html")
	public ModelAndView hehe(ModelAndView modelAndView, HttpServletRequest request) {
		return modelAndView;
	}
}
