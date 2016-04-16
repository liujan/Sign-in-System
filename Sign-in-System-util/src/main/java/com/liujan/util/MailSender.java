package com.liujan.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailSender {
	private Logger logger = LoggerFactory.getLogger(MailSender.class);
	@Autowired
	private transient MailAuthenticator authenticator;
	private transient Session session;
	private final transient Properties properties = System.getProperties();
	
	public void send(String receiver, String subject, Object content)  {
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.host", authenticator.getHostName());
		session = Session.getInstance(properties, authenticator);

		final MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(authenticator.getUserName()));
			message.setRecipient(RecipientType.TO, new InternetAddress(receiver));
			message.setSubject(subject);
			message.setContent(content, "text/html;charset=utf-8");
			Transport.send(message);
		} catch (MessagingException e) {
			logger.error(e.getMessage());
		}

	}
}
