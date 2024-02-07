package com.scm.service;
import com.scm.entities.User;

public interface UserService {
	public User save(User user);
	public User getUserByUserName(String email);
	public User setDefaultImageUrl(User user);
}
