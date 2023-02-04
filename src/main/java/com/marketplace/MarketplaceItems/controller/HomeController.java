package com.marketplace.MarketplaceItems.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	// create a mapping for "/hello"
	
	@GetMapping("/")
	public String sayHello(Model theModel) {
		return "home";
	}
}








