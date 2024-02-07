package com.scm.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.entities.User;
import com.scm.helper.Message;
import com.scm.helper.SCMConstants;
import com.scm.service.UserService;

@Controller
@RequestMapping("/")
public class HomeController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@GetMapping
	public String homePage(Model model) {
//		if(principal != null && principal.getName() != null) {
//			return "redirect:/user/index";
//		}
		model.addAttribute("title", "HOME");
		return "home";
	}
	
	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "ABOUT");
		return "about";
	}
	
	@GetMapping("/signup")
	public String signUp(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("title", "SIGN UP");
		return "signup";
	}
	
	// BindingResult should be passed next to the ModelAttribute.
	// Otherwise it will not work
	@PostMapping("/register")
	public String register(Model model, @Valid @ModelAttribute("user") User user, BindingResult br,
							@RequestParam(value="agreement", defaultValue = "false") boolean agreement,
							HttpSession session) {
		try {
			if(br.hasErrors()) {
				return "signup";
			}
			if(!agreement) {
				model.addAttribute("title", "SIGN UP");
				throw new Exception("You have not agreed to the terms and conditions!!");
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setPasswd(passwordEncoder.encode(user.getPasswd()));
			user.setImageUrl(SCMConstants.DEFAULT_PROFILE_PIC);
			userService.save(user);
		} catch(Exception e) {
			model.addAttribute("title", "Sign Up Failed");
			model.addAttribute("user", user);
			session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));
			return "signup";
		}
		
		model.addAttribute("user", new User());
		model.addAttribute("title", "Sign Up Success");
		session.setAttribute("message", new Message("Sign Up Success!! Click on Login!!!!",
														"alert-success"));
		return "signup";
	}
	
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title", "SIGN IN PAGE");
		return "login";
	}
}
