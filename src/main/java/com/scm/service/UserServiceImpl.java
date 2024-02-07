package com.scm.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scm.dao.UserRepository;
import com.scm.entities.User;
import com.scm.helper.SCMConstants;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	@Transactional
	public User save(User user) {
		return userRepository.save(user);
	}

	@Override
	@Transactional
	public User getUserByUserName(String email) {
		return userRepository.getUserByUserName(email);
	}

	@Override
	@Transactional
	public User setDefaultImageUrl(User user) {
		user.setImageUrl(SCMConstants.DEFAULT_PROFILE_PIC);
		return userRepository.save(user);
	}

}
