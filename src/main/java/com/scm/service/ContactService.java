package com.scm.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.scm.entities.Contact;
import com.scm.entities.User;

public interface ContactService {

	public Page<Contact> findContactsByUser(int userId, Pageable pageable);
	public Contact findById(int cId);
	public void deleteById(int cId);
	public void delete(Contact contact);
	public Contact save(Contact contact);
	public void deleteContactFromUser(User user, Contact contact);
	public List<Contact> findByNameContainingAndUser(String keyword, User user);
	
}
