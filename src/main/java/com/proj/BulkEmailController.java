package com.proj;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class BulkEmailController {

	private String myGmail;
	private String myPassword;
	private Session session;
	private RecipientDetails recipientDetails;
	private List<InternetAddress> rAddresses;

	private int threadCount = 1;
	private int count;
	private int failedMessages;
	private String status;

	public BulkEmailController(String myGmail, String myPassword) {
		this.myGmail = myGmail;
		this.myPassword = myPassword;
		this.rAddresses = new ArrayList<>();
		this.recipientDetails = new RecipientDetails();
	}

	// can explicitly set the thread count (default = 1)
	public void setThreadCount(int threadCount) {
		this.threadCount = (threadCount<=0) ? 1 : (threadCount>20) ? 20 : threadCount;
	}

	// setRecipients()
	public void sendBulkMail(Set<String> recipients) throws IOException{

		// loads the neccessary properties to connect to the smtp server from file
		Properties props = new Properties();
		props.load(new FileInputStream("config.properties"));

		session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(myGmail, myPassword);
			}
		});

		// coverting the recipient addresses from type 'String' to type 'InternetAddress'
		covertToAddress(recipients);

		// starts the process using the given amount of threads
		startThreadOperation();

	}

	private void covertToAddress(Set<String> recipients) {

		recipients.forEach((r) -> {
			try {
				rAddresses.add(new InternetAddress(r));
			} catch (AddressException e) {
				System.out.println("Invalid Address detected !!!\nAddress : " + r);
			}
		});
	}

	private void startThreadOperation() {

		// initializing thread count and details displayed
		System.out.println("Total no.of recipients: " + this.rAddresses.size());
		System.out.println("Thread Count : "+this.threadCount);
		System.out.println("Preparing the messages ...\n----------------------------------------------");

		// process starts
		long startTime = System.currentTimeMillis();

		Thread[] threads = new Thread[threadCount];

		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new MyRunnable());
			threads[i].start();
		}

		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// prints the results of the progress
		printResults(startTime);
	}

	private void printResults(long startTime) {
		
		this.status = (count == 0) ? "NOT OK" : 			// no recipients to send - NOT OK
			(rAddresses.size() == count) ? "SUCCESS" : 		// sent to all recipients - SUCCESS
			(this.status != null) ? this.status : 			// failed to send for some recipients - OK
			"ERROR";										// none of these - ERROR

		System.out.println("----------------------------------------------\n");
		System.out.printf(
				"Total recipients: %s\nSent : %s,\tFailed : %s,\tStatus : %s\nTime taken : %ds\nNo.Of Threads used : %d\n",
				rAddresses.size(), count - failedMessages, failedMessages, status,
				(System.currentTimeMillis() - startTime) / 1000, threadCount);
	}

	// connect() is responsible for creating [i.e via createMessage()] and sending
	// message
	private void send(InternetAddress recipient) {

		// convenience class
		try {
			recipientDetails = recipientDetails.clone();
			recipientDetails.setEmail(recipient.toString());
		} catch (CloneNotSupportedException e1) {
			e1.printStackTrace();
		}

		try {

			Message message = createMessage(recipient);
			Thread.sleep(1000);
//			Transport.send(message);
			System.out.println(recipientDetails.getMessage());
			System.out.printf("Message successfully sent to <%s>\n", recipient);
		} catch (Exception e) {
			System.out.printf("Failed to send message to : <%s>\t\t\t", recipient);
			System.out.println("Problem : " + e.getMessage());
			failedMessages++;
			this.status = "OK";			// sets the status to OK if atleast 1 failed msg
		}
	}

	// creates the message body
	private Message createMessage(InternetAddress recipient) {

		Message message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(myGmail));
			message.setRecipient(Message.RecipientType.TO, recipient);
			message.setSubject("Made use of the util package's property file handling");
			message.setText(recipientDetails.getMessage());
			return message;
		} catch (Exception e) {
			System.out.printf("SOME ERROR OCCURED IN CREATING EMAIL FOR %s!!!", recipient);
			e.printStackTrace();
		}

		return message;
	}

	private class MyRunnable implements Runnable {

		@Override
		public void run() {
			for (; count < rAddresses.size();) {

				int localCount = 0;
				synchronized (this) {
					localCount = count;
					++count;
				}
				send(rAddresses.get(localCount));
			}
		}
	}
}

