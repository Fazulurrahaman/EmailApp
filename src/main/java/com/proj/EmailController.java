package com.proj;

import java.util.Properties;
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

public class EmailController {

	private Session session;
	private RecipientDetails recipientDetails;
	private String fromEmail;
	private String password;
	
	public EmailController(String fromEmail, String password) {
		this.fromEmail = fromEmail;
		this.password = password;
		this.recipientDetails = new RecipientDetails();
	}

	public boolean sendMail(String recipient) throws CloneNotSupportedException{
		Properties props = new Properties();

		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail,password);
			}
		});
		
		return connect(recipient);
	}

	private boolean connect(String recipient) throws CloneNotSupportedException {
		
		recipientDetails = recipientDetails.clone();
		recipientDetails.setEmail(recipient);
		
		try {
			
			Message message = createMessage(recipient);
			Transport.send(message);
			System.out.println("MESSAGE SENT SUCCESSFULLY to "+recipientDetails.getRecieverName()+" !!!");
			return true;
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
			e.printStackTrace();
			return false;
		} 
	}

	private Message createMessage(String recipient) {
		
		Message message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress());
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
			message.setSubject("Generated email through properties file");
			message.setText(recipientDetails.getMessage());
			
			
			
//			// adding attachment
//			{
//				Multipart multipart = new MimeMultipart();
//				
//				// text part
//				MimeBodyPart text = new MimeBodyPart();
//				text.setText(recipientDetails.getMessage());
//				
//				// excel attachment
//				MimeBodyPart excel = new MimeBodyPart();
//				excel.attachFile("data/data.xls");
//				
//				// pdf atachment
//				MimeBodyPart certificate = new MimeBodyPart();
//				certificate.attachFile("data/c_1_pdf_template.pdf");
//
//				multipart.addBodyPart(text);
//				multipart.addBodyPart(excel);
//				multipart.addBodyPart(certificate);
//				
//				message.setContent(multipart);
//			}
			
			
			System.out.println("MESSAGE GENERATED for "+recipientDetails.getRecieverName()+" !!!");
			return message;
		} catch (Exception e) {
			System.out.println("SOME ERROR OCCURED !!!");
			e.printStackTrace();
		}
		
		return message;
	}

}
