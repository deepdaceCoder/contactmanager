package com.scm.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "CONTACT")
public class Contact {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cId;
	@Column
	@NotEmpty(message="Name cannot be empty!!")
	private String name;
	@Column
	private String secondName;
	@Column
	@NotEmpty(message="Designation cannot be empty!!")
	private String work;
	@Column(unique = true)
	@NotBlank(message = "Email cannot be empty!!")
	@Email(message = "It should be email format!!")
	@Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
			+ "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", message = "Invalid Email Format!!")
	private String email;
	@Column
	@Size(min = 10, max = 10, message = "Phone number should be 10 digits!!")
	@Pattern(regexp="[\\d]{10}", message = "It should be only numbers!!")
	private String phone;
	
	@Column
	private String image;
	
	@Column(length = 1000)
	@Size(max = 1000, message = "Description cannot exceed 1000 letters!!")
	private String description;
	
	// JSON Ignore to ignore json serialize
	@ManyToOne
	@JsonIgnore
	private User user;
	
	public Contact() {
		super();
	}

	public Contact(int cId, String name, String secondName, String work, String email, String phone, String image,
			String description) {
		super();
		this.cId = cId;
		this.name = name;
		this.secondName = secondName;
		this.work = work;
		this.email = email;
		this.phone = phone;
		this.image = image;
		this.description = description;
	}

	public int getcId() {
		return cId;
	}

	public void setcId(int cId) {
		this.cId = cId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Contact [cId=" + cId
				+ ", name=" + name
				+ ", secondName=" + secondName
				+ ", work=" + work
				+ ", email=" + email
				+ ", phone=" + phone
				+ ", image=" + image
				+ ", description=" + description + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.cId == ((Contact)obj).getcId();
	}
	
	
}
