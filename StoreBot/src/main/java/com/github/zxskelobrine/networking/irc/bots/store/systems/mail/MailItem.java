package com.github.zxskelobrine.networking.irc.bots.store.systems.mail;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MailItem {

	private String message;
	private String sender;
	private String recipient;
	private String time;

	public MailItem(String sender, String message, String recipient) {
		this.sender = sender;
		this.message = message;
		this.recipient = recipient;
		this.time = new SimpleDateFormat("dd-MM-yy - hh:mm").format(Calendar.getInstance().getTime());
	}

	public String[] generateMessage() {
		return new String[] { "Dear " + recipient, "\t" + message, "From " + sender };
	}

	public String getRecipient() {
		return recipient;
	}

	@Override
	public String toString() {
		String[] total = generateMessage();
		return total[0] + "\n" + total[1] + "\n" + total[2];
	}

}
