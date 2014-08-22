package com.github.zxskelobrine.networking.irc.bots.store.managers.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.zxskelobrine.networking.irc.bots.store.systems.SystemsManager;
import com.github.zxskelobrine.networking.irc.bots.store.systems.karma.KarmaUser;
import com.github.zxskelobrine.networking.irc.bots.store.systems.mail.MailItem;

public class PersistentDataManager {

    private static String PERSISTENT_MAIL_BASE = "E:\\IRC Logs\\Bot Store\\Quakenet\\Project Awesome\\VitBot\\Persistent\\Mail\\";
    private static String PERSISTENT_KARMA_BASE = "E:\\IRC Logs\\Bot Store\\Quakenet\\Project Awesome\\VitBot\\Persistent\\Karma\\";
    private static String PERSISTENT_SETTINGS_BASE = "E:\\IRC Logs\\Bot Store\\Quakenet\\Project Awesome\\VitBot\\Persistent\\Settings\\";

    private static FileOutputStream currentOutput;
    private static BufferedReader currentBufferedInput;

    enum PersistentDataType {
	MAIL, KARMA, SETTINGS;
    }

    public static void setDataPaths(String hostname, String channel) {
	PERSISTENT_MAIL_BASE = "IRC Logs\\Bot Store\\" + hostname + "\\"
		+ channel + "\\VitBot\\Persistent\\Mail\\";
	PERSISTENT_KARMA_BASE = "IRC Logs\\Bot Store\\" + hostname + "\\"
		+ channel + "\\VitBot\\Persistent\\Karma\\";
	PERSISTENT_SETTINGS_BASE = "IRC Logs\\Bot Store\\" + hostname + "\\"
		+ channel + "\\VitBot\\Persistent\\Settings\\";
	File settings = new File(PERSISTENT_SETTINGS_BASE + "settings.txt");
	if (!settings.exists())
	    generateFolderStructure();
    }

    private static void generateFolderStructure() {
	File file = new File(PERSISTENT_KARMA_BASE);
	file.mkdirs();
	file = new File(PERSISTENT_MAIL_BASE);
	file.mkdirs();
	file = new File(PERSISTENT_SETTINGS_BASE);
	file.mkdirs();
	file = new File(PERSISTENT_SETTINGS_BASE + "settings.txt");
	try {
	    file.createNewFile();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	saveSettingsToFile();
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
	List<KarmaUser> karmaUsers = new ArrayList<KarmaUser>();
	File file = new File(PERSISTENT_KARMA_BASE);
	if (file.listFiles() != null) {
	    for (File karma : file.listFiles()) {
		if (karma.getName()
			.substring(karma.getName().lastIndexOf(".") + 1)
			.equalsIgnoreCase("txt")) {
		    formBufferedInputStream(karma);
		    try {
			String karmaAmountAS = currentBufferedInput.readLine();
			if (karmaAmountAS != null) {
			    int karmaValue = Integer.parseInt(karmaAmountAS);
			    String name = karma.getName().substring(0,
				    karma.getName().lastIndexOf("."));
			    KarmaUser user = new KarmaUser(name, karmaValue);
			    karmaUsers.add(user);
			}
		    } catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		    }
		}
	    }
	}
	return karmaUsers;
    }

    private static void formFileOutputStream(String nick,
	    PersistentDataType dataType) throws IOException {
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
	case SETTINGS:
	    file = new File(PERSISTENT_SETTINGS_BASE + nick + ".txt");
	    append = false;
	default:
	    break;
	}
	if (!file.getParentFile().exists())
	    file.getParentFile().mkdirs();
	if (!file.exists()) {
	    file.createNewFile();
	}
	currentOutput = new FileOutputStream(file, append);
    }

    private static void formBufferedInputStream(String nick,
	    PersistentDataType dataType) throws FileNotFoundException {
	File file = null;
	switch (dataType) {
	case KARMA:
	    file = new File(PERSISTENT_KARMA_BASE + nick + ".txt");
	    break;
	case MAIL:
	    file = new File(PERSISTENT_MAIL_BASE + nick + ".txt");
	    break;
	case SETTINGS:
	    file = new File(PERSISTENT_SETTINGS_BASE + nick + ".txt");
	default:
	    break;
	}
	if (file.exists()) {
	    currentBufferedInput = new BufferedReader(new FileReader(file));
	}
    }

    private static void formBufferedInputStream(File file) {
	try {
	    currentBufferedInput = new BufferedReader(new FileReader(file));
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
    }

    public static void saveSettingsToFile() {
	try {
	    formFileOutputStream("settings", PersistentDataType.SETTINGS);
	    currentOutput.write((getBooleanString(SystemsManager
		    .isIdiotSystemEnabled()) + "\n").getBytes());
	    currentOutput.write((getBooleanString(SystemsManager
		    .isRiotSystemEnabled()) + "\n").getBytes());
	    currentOutput.write((getBooleanString(SystemsManager
		    .isLyricSystemEnabled()) + "\n").getBytes());
	    currentOutput.write((getBooleanString(SystemsManager
		    .isNorwaySystemEnabled()) + "\n").getBytes());
	    currentOutput.write((getBooleanString(SystemsManager
		    .isMailSystemEnabled()) + "\n").getBytes());
	    currentOutput.write((getBooleanString(SystemsManager
		    .isKarmaSystemEnabled()) + "\n").getBytes());
	    currentOutput.write((getBooleanString(SystemsManager
		    .isInsultsSystemEnabled()) + "\n").getBytes());
	    currentOutput.write((getBooleanString(SystemsManager
		    .isSlapSystemEnabled()) + "\n").getBytes());
	    currentOutput.write((getBooleanString(SystemsManager
		    .isPartySystemEnabled())).getBytes());
	    currentOutput.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    private static String getBooleanString(boolean bool) {
	return bool ? "true" : "false";
    }

    private static boolean getStringBoolean(String string) {
	return string.equalsIgnoreCase("true") ? true : false;
    }

    public static void loadSettingsFromFile() {
	try {
	    formBufferedInputStream("settings", PersistentDataType.SETTINGS);
	    String fl = currentBufferedInput.readLine();
	    SystemsManager.setIdiotSystem(getStringBoolean(fl));
	    SystemsManager.setRiotSystem(getStringBoolean(currentBufferedInput
		    .readLine()));
	    SystemsManager.setLyricSystem(getStringBoolean(currentBufferedInput
		    .readLine()));
	    SystemsManager
		    .setNorwaySystem(getStringBoolean(currentBufferedInput
			    .readLine()));
	    SystemsManager.setMailSystem(getStringBoolean(currentBufferedInput
		    .readLine()));
	    SystemsManager.setKarmaSystem(getStringBoolean(currentBufferedInput
		    .readLine()));
	    SystemsManager
		    .setInsultsSystem(getStringBoolean(currentBufferedInput
			    .readLine()));
	    SystemsManager.setSlapSystem(getStringBoolean(currentBufferedInput
		    .readLine()));
	    SystemsManager.setPartySystem(getStringBoolean(currentBufferedInput
		    .readLine()));
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
