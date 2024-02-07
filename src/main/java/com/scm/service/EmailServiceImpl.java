package com.scm.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

	@Override
	public boolean sendEmail(String subject, String message, String to) {
		
		try {

			String from = "abc@gmail.com";
			String host = "smtp.gmail.com";

			Properties properties = System.getProperties();

			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", "465");
			properties.put("mail.smtp.ssl.enable", "true");
			properties.put("mail.smtp.auth", "true");

			// Get session object
			Session session = Session.getInstance(properties, new Authenticator() {

				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(from, "*****");
				}

			});

			session.setDebug(true);

			// Compose the message
			MimeMessage m = new MimeMessage(session);
			m.setFrom(from);
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			m.setSubject(subject);
			m.setContent(message, "text/html");
			
			// Send message using transport class
			Transport.send(m);
		
			return true;
		} catch(Exception e) {
			return false;
		}
	}

}
