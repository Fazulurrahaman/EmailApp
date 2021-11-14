package com.proj;

public class RecipientDetails implements Cloneable {

	private String recieverName;
	private String email;
	
	public void setEmail(String email) {
		this.email = email;
		this.recieverName = this.email.split("@")[0];
	}

	public String getMessage() {
		return "Dear Mr. " + this.recieverName;
//				return "Dear Mr. " + this.recieverName + ",\n"
//				+ "\tGreetings to you. I hope you are at the best of your health. "
//				+ "\nWelcome to my GitHub account - https://github.com/Aslam-web/EmailApplication"
//
//				+ "\n\n\nThanks & Regards" + "\nMr M.N Aslam," + "\nJAVA developer Trainer,"
//				+ "\nHaaris Infotech Institutions," + "\nEmail : aslam1qqqq@gmail.com," + "\nPhone: +91 63799 71782.";
	}

	public String getRecieverName() {
		return recieverName;
	}
	
	@Override
	protected RecipientDetails clone() throws CloneNotSupportedException {
		return (RecipientDetails) super.clone();
	}
}
