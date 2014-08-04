package com.github.zxskelobrine.networking.irc.bots.store;

public class MailItem {

	private String message;
	private String sender;
	private String recipient;

	public MailItem(String sender, String message, String recipient) {
		this.sender = sender;
		this.message = message;
		this.recipient = recipient;
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
