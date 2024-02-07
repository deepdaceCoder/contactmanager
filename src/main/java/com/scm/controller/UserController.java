package com.scm.controller;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.helper.Counter;
import com.scm.helper.Message;
import com.scm.helper.SCMConstants;
import com.scm.service.ContactService;
import com.scm.service.UserService;
import com.scm.util.UploadHelper;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ContactService contactService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@ModelAttribute
	private void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		User user = userService.getUserByUserName(userName);
		
		if(user.getImageUrl() == null || user.getImageUrl().isEmpty()) {
			user = userService.setDefaultImageUrl(user);
		}
		
		model.addAttribute("user", user);
		model.addAttribute("defaultPicture", SCMConstants.DEFAULT_PROFILE_PIC);
		model.addAttribute("home", true);
		model.addAttribute("profile", false);
	}
	
	@GetMapping("/index")
	public String dashboard(Model model) {
		model.addAttribute("title", "HOME PAGE");
		model.addAttribute("home", true);
		model.addAttribute("profile", false);
		return "normal/user_dashboard";
	}
	
	@GetMapping("/add-contact")
	public String openAddContactForm( @RequestParam("cId") Optional<String> cIdOpt, Model model, 
										Principal principal) {
		String cId = cIdOpt.orElse(null);
		System.out.println("Update user contact cId - " + cId);
		if(cId != null) {
			String userName = principal.getName();
			User user = userService.getUserByUserName(userName);
			Contact contact = contactService.findById(Integer.valueOf(cId));
			if(user.getId() == contact.getUser().getId()) {
				model.addAttribute("title", "UPDATE CONTACT");
				model.addAttribute("contact", contact);
				model.addAttribute("mode", "update");
			} else {
				model.addAttribute("contact", new Contact());
			}
				
		} else {
			model.addAttribute("title", "ADD CONTACT");
			model.addAttribute("contact", new Contact());
		}
		return "normal/add_contact_form";
	}
	
	@PostMapping("/process-contact")
	public String processContact(@RequestParam("imageFile") MultipartFile imageFile,
								@ModelAttribute @Valid Contact contact, BindingResult br,
								Model model, HttpSession session, Principal principal) {

		System.out.println("From form - " + contact);
		
		try {
			
			if(br.hasErrors()) {
				System.out.println(br);
				model.addAttribute("title", "Contact Add Failed");
				model.addAttribute("contact", contact);
				session.setAttribute("message", new Message("Error in data!!", "alert-danger"));
				return "normal/add_contact_form";
			}
			
			String userName = principal.getName();
			User user = userService.getUserByUserName(userName);
			
			if(!imageFile.isEmpty()) {
				
				String dir = new ClassPathResource("static/images").getFile().getAbsolutePath() 
								+ File.separator + user.getId();
				File dirFile = new File(dir);
				UploadHelper.createDir(dirFile);
				
				String imageFinalName = imageFile.getOriginalFilename().length() > 5 ? 
										imageFile.getOriginalFilename().substring(0,5) + "_"
										+ System.currentTimeMillis() 
										: imageFile.getOriginalFilename() + "_" 
										+ System.currentTimeMillis();
				
				imageFinalName = imageFinalName + "." 
										+ FilenameUtils.getExtension(imageFile.getOriginalFilename());
				try {
					UploadHelper.copyFile(imageFile, dir + File.separator + imageFinalName);
					contact.setImage(imageFinalName);
					
					//Run when case is of update
					if(contact.getcId()!=0) {
						Contact oldContact = contactService.findById(contact.getcId());
						String image = new ClassPathResource("static/images").getFile().getAbsolutePath() 
								+ File.separator + user.getId() + File.separator + oldContact.getImage();
						UploadHelper.deleteImage(image);
					}
					
				} catch(IOException e) {
					contact.setImage(SCMConstants.DEFAULT_PROFILE_PIC);
					throw new Exception("Error in uploading image!!");
				}
				
			} else {
				
				System.out.println("No file uploaded");
				if(contact.getcId() == 0)
					contact.setImage(SCMConstants.DEFAULT_PROFILE_PIC);
				else {
					Contact oldContact = contactService.findById(contact.getcId());
					contact.setImage(oldContact.getImage());
				}
				
			}
			
			contact.setUser(user);
			if(contact.getcId() == 0) {
				System.out.println("Add new user!!");
				user.getContacts().add(contact);
				userService.save(user);

				System.out.println("Successfully added");
				model.addAttribute("title", "Successfully Added!!");
				session.setAttribute("message", new Message("Contact Added Successfully!!",
																"alert-success"));
			} else {
				System.out.println("Update user!!");
				contactService.save(contact);
				
				System.out.println("Successfully updated");
				model.addAttribute("title", "Successfully Updated!!");
				session.setAttribute("message", new Message("Contact Updated Successfully!!", 
																"alert-success"));
				return "redirect:/user/show-contacts/detail/" + contact.getcId();
			}
			
		} catch (Exception e) {
			System.out.println("Exception caught!!");
			e.printStackTrace();
			model.addAttribute("title", "Contact Add Failed");
			model.addAttribute("contact", contact);
			session.setAttribute("message", new Message("INTERNAL ERROR!!", "alert-danger"));
			return "normal/add_contact_form";
		}
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}
	
	/**
	 * Show contacts
	 * Per page 3
	 * current page = 0
	 * @param model
	 * @param principal
	 * @return
	 */
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
		
		String userName = principal.getName();
		User user = userService.getUserByUserName(userName);
		
		Pageable pageable = PageRequest.of(page, SCMConstants.CONTACT_PER_PAGE);
		Page<Contact> contacts = contactService.findContactsByUser(user.getId(), pageable);
		
		model.addAttribute("title", "View Contacts");
		if(contacts != null && !contacts.isEmpty()) {
			model.addAttribute("contacts", contacts);
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", contacts.getTotalPages());
			model.addAttribute("counter", new Counter((page * SCMConstants.CONTACT_PER_PAGE) + 1));
		}
		
		model.addAttribute("title", "Contacts");
		return "normal/show_contacts";
	}
	
	@GetMapping("/show-contacts/detail/{cId}") 
	public String showContactDetails(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		
		String userName = principal.getName();
		User user = userService.getUserByUserName(userName);
		
		Contact contact = contactService.findById(cId);
		if(contact != null && user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}
		
		model.addAttribute("title", "Contact Details");
		return "normal/contact_detail";
	}
	
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, Model model, Principal principal,
									HttpSession session) {
		
		String userName = principal.getName();
		User user = userService.getUserByUserName(userName);
		Contact contact = contactService.findById(cId);
		
		try {
			
			if(contact!=null && user.getId() == contact.getUser().getId()) {
				
				if(!contact.getImage().equals(SCMConstants.DEFAULT_PROFILE_PIC)) {
					String image = new ClassPathResource("static/images").getFile().getAbsolutePath() 
							+ File.separator + user.getId() + File.separator + contact.getImage();
					UploadHelper.deleteImage(image);
				}
				
				contactService.deleteContactFromUser(user, contact);
				contactService.delete(contact);
				
			} else {
				session.setAttribute("message", new Message("Failed to delete", "alert-danger"));
			}
			
		} catch (Exception e) {
			session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));
		}
		
		session.setAttribute("message", new Message("Contact Deleted Successfully!!", "alert-success"));
		model.addAttribute("title", "Contacts");
		return "redirect:/user/show-contacts/0";
	}
	
	@GetMapping("/profile")
	public String userProfile(Model model) {
		model.addAttribute("title", "Profile Page");
		model.addAttribute("home", false);
		model.addAttribute("profile", true);
		return "normal/profile";
	}
	
	@GetMapping("/settings")
	public String openSettings(Model model) {
		model.addAttribute("title", "User Settings");
		return "normal/settings";
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword, 
									@RequestParam("newPassword") String newPassword, Principal principal,
									HttpSession session, RedirectAttributes redirectAttrs) {
		
		String userName = principal.getName();
		User user = userService.getUserByUserName(userName);
		
		// If matches then change the password
		if(this.passwordEncoder.matches(oldPassword, user.getPasswd())) {
			
			if(Pattern.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
					newPassword)) {
				user.setPasswd(this.passwordEncoder.encode(newPassword));
				this.userService.save(user);
				session.setAttribute("message", 
							new Message("Your password is successfully changed!!", "alert-success"));
			} else {
				session.setAttribute("message", 
						new Message("Your new password pattern is wrong!!", "alert-danger"));
				return "redirect:/user/settings";
			}
		} else {
			session.setAttribute("message", 
					new Message("Your old password is wrong!!", "alert-danger"));
			return "redirect:/user/settings";
		}
		
		redirectAttrs.addFlashAttribute("cp", true);
		return "redirect:/user/settings";
	}
	
}
