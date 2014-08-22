package com.github.zxskelobrine.networking.irc.bots.store.systems.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.pircbotx.User;

import com.github.zxskelobrine.networking.irc.bots.store.managers.internal.PersistentDataManager;

public class MailManager {

	private static List<MailItem> mail = new ArrayList<MailItem>();

	public synchronized static void addMail(MailItem item) {
		synchronized (mail) {
			mail.add(item);
		}
	}

	public synchronized static void mailSent(MailItem item) {
		synchronized (mail) {
			mail.remove(item);
		}
	}

	public synchronized static boolean hasMail(String nick) {
		synchronized (mail) {
			CopyOnWriteArrayList<MailItem> mails = new CopyOnWriteArrayList<>(mail);
			for (MailItem item : mails) {
				if (item.getRecipient().equalsIgnoreCase(nick)) {
					return true;
				}
			}
			return false;
		}
	}

	public synchronized static void sendMail(User user) {
		synchronized (mail) {
			CopyOnWriteArrayList<MailItem> mails = new CopyOnWriteArrayList<>(mail);
			for (MailItem item : mails) {
				if (item.getRecipient().equalsIgnoreCase(user.getNick())) {
					for (String s : item.generateMessage()) {
						user.send().message(s);
					}
					mailSent(item);
				}
			}
		}
	}

	public synchronized static void saveAllMail() {
		synchronized (mail) {
			CopyOnWriteArrayList<MailItem> items = new CopyOnWriteArrayList<>(mail);
			for (MailItem item : items) {
				PersistentDataManager.storeMail(item.getRecipient(), item);
			}
		}
	}
	
	public synchronized static void readMail(String nick){
		
	}
}
