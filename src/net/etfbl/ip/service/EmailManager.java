package net.etfbl.ip.service;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailManager {

	private Properties loadEmailProperties() {
		Properties props = new Properties();
		try (InputStream input = getClass().getClassLoader()
				.getResourceAsStream("net/etfbl/ip/utils/email.properties")) {
			if (input == null) {
				return null;
			}
			props.load(input);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return props;
	}

	public void sendEmailAsync(String recipient, String subject, String content, List<File> attachments) {
		Thread emailThread = new Thread(() -> {
			try {
				sendEmail(recipient, subject, content, attachments);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		emailThread.start();
	}

	public void sendEmail(String recipient, String subject, String content, List<File> attachments) {
		Properties emailProps = this.loadEmailProperties();

		if (emailProps == null) {
			return;
		}

		final String username = emailProps.getProperty("mail.smtp.user");
		final String password = emailProps.getProperty("mail.smtp.password");
		Session session = Session.getInstance(emailProps, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
			message.setSubject(subject);

			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(content);

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			if (attachments != null && !attachments.isEmpty()) {
				for (File attachment : attachments) {
					MimeBodyPart attachmentPart = new MimeBodyPart();
					FileDataSource source = new FileDataSource(attachment);
					attachmentPart.setDataHandler(new DataHandler(source));
					attachmentPart.setFileName(attachment.getName());
					multipart.addBodyPart(attachmentPart);
				}
			}

			message.setContent(multipart);

			Transport.send(message);

		} catch (MessagingException e) {
			e.printStackTrace();
		} finally {
			if (attachments != null) {
				for (File file : attachments) {
					if (file.exists()) {
						file.delete();
					}
				}
			}
		}
	}
}