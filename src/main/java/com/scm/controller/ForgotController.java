package com.scm.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.entities.User;
import com.scm.helper.Message;
import com.scm.service.EmailService;
import com.scm.service.UserService;

@Controller
@RequestMapping("/")
public class ForgotController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private PasswordEncoder encoder;
	
	private Random random = new Random();
	
	@GetMapping("/forgot")
	public String openEmailForm(Model model) {
		model.addAttribute("title", "FORGOT PASSWORD");
		return "forgot_email_form";
	}
	
	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email") String email, Model model, HttpSession session) {
		model.addAttribute("title", "VERIFY OTP");
		model.addAttribute("email", email);
		
		User user = userService.getUserByUserName(email);
		if(user == null) {
			session.setAttribute("message", 
					new Message("There is no user with this email ID!!", "alert-danger"));
			return "forgot_email_form";
		}
		
		int otp = random.nextInt(100000, 999999);
		System.out.println(otp);
		String message = "Your OTP for password reset is - " + otp;
		boolean flag = emailService.sendEmail("OTP - Smart Contact Manager", message, email);
		flag = true;
		if(flag) {
			session.setAttribute("serverOtp", otp);
			session.setAttribute("email", email);
			session.setAttribute("message", 
					new Message("We have successfully sent OTP to your email!!", "alert-success"));
			return "verify_otp";
		} else {
			session.setAttribute("message", 
					new Message("Check your email!!", "alert-danger"));
			return "forgot_email_form";
		}
		
	}
	
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") Integer otp, @RequestParam("email") String email,
			Model model,  
			HttpSession session) {
		Integer serverOtp = (Integer) session.getAttribute("serverOtp");
		model.addAttribute("title", "RESET PASSWORD");
		model.addAttribute("email", email);
		
		System.out.println("User OTP - " + otp);
		System.out.println("serverOtp - " + serverOtp);
		
		if(otp.intValue() == serverOtp.intValue()) {
			session.setAttribute("message", 
					new Message("OTP Verified!!", "alert-success"));
			return "password_change_form";
		} else {
			session.setAttribute("message", 
					new Message("OTP is wrong!!", "alert-danger"));
			return "forgot_email_form";
		}
		
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("passwd") String newPassword, @RequestParam("email") String email, 
									HttpSession session, 
									Model model) {
		User user = userService.getUserByUserName(email);
		user.setPasswd(encoder.encode(newPassword));
		userService.save(user);
		return "redirect:/signin?change=pass";
	}
	
}
