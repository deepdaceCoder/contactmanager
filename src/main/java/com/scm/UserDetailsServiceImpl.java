package com.scm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.scm.entities.User;
import com.scm.service.UserService;

public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserService userService;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// fetching user from database
		
		User user = userService.getUserByUserName(username);
		
		if(user == null) {
			throw new UsernameNotFoundException("Could not find user!!");
		}
		
		CustomUserDetails customerUserDetails = new CustomUserDetails(user);
		
		return customerUserDetails;
	}

}
