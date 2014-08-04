package com.github.zxskelobrine.networking.irc.bots.store.managers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.zxskelobrine.networking.irc.bots.store.systems.karma.KarmaUser;
import com.github.zxskelobrine.networking.irc.bots.store.systems.mail.MailItem;

public class PersistentDataManager {

	private static final String PERSISTENT_MAIL_BASE = "E:\\IRC Logs\\Bot Store\\Quakenet\\Project Awesome\\VitBot\\Persistent\\Mail\\";//TODO Change to sef gen path.
	private static final String PERSISTENT_KARMA_BASE = "E:\\IRC Logs\\Bot Store\\Quakenet\\Project Awesome\\VitBot\\Persistent\\Karma\\";//TODO Change to sef gen path.
	private static FileOutputStream currentOutput;
	private static BufferedReader currentBufferedInput;

	enum PersistentDataType {
		MAIL, KARMA;
	}

	public static void storeMail(String nick, MailItem item) {
		try {
			formFileOutputStream(nick, PersistentDataType.MAIL);
			String[] mail = item.generateMessage();
			currentOutput.write((mail[0] + "\n").getBytes());
			currentOutput.write((mail[1] + "\n").getBytes());
			currentOutput.write((mail[2] + "\n").getBytes());
			currentOutput.flush();
			currentOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void storeKarma(KarmaUser user) {
		try {
			formFileOutputStream(user.getNick(), PersistentDataType.KARMA);
			currentOutput.write(("" + user.getKarma()).getBytes());
			currentOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<MailItem> getMailItems(String nick) {
		try {
			List<MailItem> items = new ArrayList<MailItem>();
			formBufferedInputStream(nick, PersistentDataType.MAIL);
			int lineID = 0;
			String line;
			MailItem item;
			String from;
			String to = null;
			String message = null;
			while ((line = currentBufferedInput.readLine()) != null) {
				int div = (lineID + 1) % 3;
				if (div == 0) {
					from = line.split("From")[1].substring(1);
					item = new MailItem(from, message, to);
					items.add(item);
					item = null;
				}
				if (div == 1) {
					to = line.split("Dear")[1].substring(1);
				}
				if (div == 2) {
					message = line;
				}
				lineID++;
			}
			return items;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<KarmaUser> getKarmaUsers() {

		return null;
	}
	
	private static void formFileOutputStream(String nick, PersistentDataType dataType) throws IOException {
		File file = null;
		boolean append = true;
		switch (dataType) {
		case KARMA:
			file = new File(PERSISTENT_KARMA_BASE + nick + ".txt");
			append = false;
			break;
		case MAIL:
			file = new File(PERSISTENT_MAIL_BASE + nick + ".txt");
			break;
		default:
			break;
		}
		if (!file.exists()) {
			file.createNewFile();
		}
		currentOutput = new FileOutputStream(file, append);
	}

	private static void formBufferedInputStream(String nick, PersistentDataType dataType) throws FileNotFoundException {
		File file = null;
		switch (dataType) {
		case KARMA:
			file = new File(PERSISTENT_KARMA_BASE + nick + ".txt");
			break;
		case MAIL:
			file = new File(PERSISTENT_MAIL_BASE + nick + ".txt");
			break;
		default:
			break;
		}
		if (file.exists()) {
			currentBufferedInput = new BufferedReader(new FileReader(file));
		}
	}
}
