package com.scm.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.scm.dao.ContactRepository;
import com.scm.entities.Contact;
import com.scm.entities.User;

@Service
public class ContactServiceImpl implements ContactService {
	
	@Autowired
	private ContactRepository contactRepository;

	@Override
	@Transactional
	public Page<Contact> findContactsByUser(int userId, Pageable pageable) {
		// TODO Auto-generated method stub
		return contactRepository.findContactsByUser(userId, pageable);
	}

	@Override
	@Transactional
	public Contact findById(int cId) {
		Optional<Contact> cOptional = contactRepository.findById(cId);
		return cOptional.orElse(null);
	}

	@Override
	@Transactional
	public void deleteById(int cId) {
		contactRepository.deleteById(cId);
	}

	@Override
	@Transactional
	public void delete(Contact contact) {
		contactRepository.delete(contact);
	}

	@Override
	@Transactional
	public Contact save(Contact contact) {
		return contactRepository.save(contact);
	}

	
	// Due to mapping with Parent and child table need to remove the contact from User first
	// Otherwise delete will not work
	// Also transactional need to be added
	@Override
	@Transactional
	public void deleteContactFromUser(User user, Contact contact) {
		user.getContacts().removeIf(c -> c.getcId() == contact.getcId());
		contact.setUser(null);
	}

	@Override
	@Transactional
	public List<Contact> findByNameContainingAndUser(String keyword, User user) {
		// TODO Auto-generated method stub
		return contactRepository.findByNameContainingAndUser(keyword, user);
	}

}
