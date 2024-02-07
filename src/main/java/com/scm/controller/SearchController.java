package com.scm.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.service.ContactService;
import com.scm.service.UserService;

@RestController
@RequestMapping("/user/search")
public class SearchController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private ContactService contactService;
	
	@GetMapping("/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal) {
		
		System.out.println("Reached search");
		String userName = principal.getName();
		User user = userService.getUserByUserName(userName);
		
		List<Contact> contactList = contactService.findByNameContainingAndUser(query, user);
		return ResponseEntity.ok(contactList);
		
	}
	
}
