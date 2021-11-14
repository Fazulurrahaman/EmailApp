package com.proj;

import com.proj.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.proj.*;
import com.proj.*;

public class App {
	
	private static Properties props = new Properties();
	
	static {
		try {
			props.load(new FileInputStream("config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String... args) throws Exception {
		String email = props.getProperty("EMAIL");
		String pass = props.getProperty("PASSWORD");
		
		BulkEmailController bController = new BulkEmailController(email, pass);
		
		bController.setThreadCount(2);
		bController.sendBulkMail(CollectionData.get());
//		
//		EmailController emailController = new EmailController(email, pass);
//		emailController.sendMail("aslam1qqqq@gmail.com");
		
//		bController.sendBulkMail(new TextFileReader().read("data/email100.txt"));
	}
}