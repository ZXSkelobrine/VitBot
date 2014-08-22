package com.github.zxskelobrine.networking.irc.bots.store;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.cap.TLSCapHandler;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import com.github.zxskelobrine.networking.irc.bots.store.managers.internal.ChatManager;
import com.github.zxskelobrine.networking.irc.bots.store.managers.internal.CoolDownManager;
import com.github.zxskelobrine.networking.irc.bots.store.managers.internal.OperatorManager;
import com.github.zxskelobrine.networking.irc.bots.store.managers.internal.PersistentDataManager;
import com.github.zxskelobrine.networking.irc.bots.store.systems.SystemsManager;
import com.github.zxskelobrine.networking.irc.bots.store.systems.karma.KarmaManager;
import com.github.zxskelobrine.networking.irc.bots.store.systems.karma.KarmaUser;
import com.github.zxskelobrine.networking.irc.bots.store.systems.mail.MailManager;

public class StoreBot extends ListenerAdapter<PircBotX> {

	/**
	 * This is the output stream to the current log file.
	 */
	private FileOutputStream fos;

	/**
	 * This is current file the messages will be logged to.
	 */
	private File output;

	/**
	 * This is the base bot that this works off
	 */
	private static PircBotX bot;

	/**
	 * This is the bots name.
	 */
	public static final String BOT_NAME = "VitBot";
	/**
	 * This is the ip mask that the bot uses:<br>
	 * ~<b><code>BOT_IP_MASK</code></b>@0540ca1a.skybroadband.com
	 */
	private static final String BOT_IP_MASK = "VitBot";

	/**
	 * This is the server that the bot will be connecting to.
	 */
	private static String hostname;
	/**
	 * This is the channel name supplied (like the hostname) from the launch
	 * arguments of the program.
	 */
	public static String channelString;
	/**
	 * This is the character that will start all commands that the bot will
	 * register.
	 */
	public static final String startCharacter = "!";

	public static List<String> insultStrings = new ArrayList<String>();
	private String[] helpStrings = new String[] { "VitBot Help: ", StoreBot.startCharacter + "help: Prints this message", StoreBot.startCharacter + "mail <To> <Message>: Will send a message to <To> and will be delivered when they join or say a message.", StoreBot.startCharacter + "-- <User>: Subtracts one karma from <User>", StoreBot.startCharacter + "++ <User>: Adds one karma to <User>", StoreBot.startCharacter + "?? <User>: Shows the current karma of <User>", StoreBot.startCharacter + "lyric: Prints a stupid lyric", StoreBot.startCharacter + "riot: Shows how many people are rioting and adds you to the current riot." };

	/**
	 * This is the channel that the bot is currently opperating on.
	 */
	public static Channel channel;
	/**
	 * This is the {@link ChatManager} that is uses to process most of the
	 * messages.
	 */
	private ChatManager chatManager;

	/**
	 * This is a joke variable used by {@link #log(String)}. Must always be true
	 * <em>duh.</em>
	 */
	static boolean isThePopeACatholic = true;

	/**
	 * This method will print the given message to sdout if it is enabled.
	 * 
	 * @param message
	 */
	private static void log(String message) {
		if (isThePopeACatholic) {
			System.out.println(message);
		}
	}

	/**
	 * This is the method used to start the bot.
	 * 
	 * @param args
	 *            - {@link String[]} - The launch arguments used to get the
	 *            hostname and channel.
	 * @param sbot
	 *            - {@link StoreBot} - This is the instance of this class used
	 *            by the bot.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void launchVitBot(String[] args, StoreBot sbot, boolean pope) {
		if (args.length >= 2) {
			hostname = args[0];
			log("Set hostnae");
			channelString = args[1];
			log("Set channel");
			isThePopeACatholic = pope;
			log("Creating config");
			Configuration<PircBotX> configuration;
			try {
				configuration = new Configuration.Builder().setName(BOT_NAME).setLogin(BOT_IP_MASK).setAutoNickChange(false).setCapEnabled(true).addCapHandler(new TLSCapHandler(new UtilSSLSocketFactory().trustAllCertificates(), true)).addListener(sbot).setServerHostname(hostname)
				// irc.quakenet.org
						.addAutoJoinChannel(channelString).setServerPassword("08348P!uiXJbeCHQ@hQj").buildConfiguration();//
				log("Config created");
				PircBotX botX = new PircBotX(configuration);
				log("Bot created");
				bot = botX;
				PersistentDataManager.getMailItems("TestAccount");
				log("Bot set");
				botX.startBot();
				log("Bot started");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IrcException e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Invalid arguments.");
		}
	}

	/**
	 * This is the only contructor for the {@link StoreBot} class.
	 * 
	 * @throws IOException
	 *             - If file creation fails.
	 */
	public StoreBot() throws IOException {
		CoolDownManager.startCooldownManager();
		File suggestionFile = new File("E:\\IRC Logs\\Bot Store\\Quakenet\\Project Awesome\\VitBot\\Suggestions\\Suggestions - " + new SimpleDateFormat("dd-MM-yy - hh").format(Calendar.getInstance().getTime()) + ".txt");
		if (!suggestionFile.exists()) suggestionFile.createNewFile();
		output = new File("E:\\IRC Logs\\Bot Store\\Quakenet\\Project Awesome\\VitBot\\" + new SimpleDateFormat("dd-MM-yy - hh").format(Calendar.getInstance().getTime()) + ".txt");
		log("Output file set");
		if (!output.exists()) output.createNewFile();
		log("Output file created if needed");
		fos = new FileOutputStream(output, true);
		log("Output stream set");
		prepareInsultStrings();
	}

	/**
	 * This method will add all of the needed strings to the
	 * {@link #insultStrings} list.
	 */
	private void prepareInsultStrings() {
		insultStrings.add("%TO%'s mum is a pinecone.");
		insultStrings.add("Shut up %TO% you goat in a waistcoat.");
		insultStrings.add("%TO% you are Adolf Titler.");
		insultStrings.add("%TO% you are a pavian.");
		insultStrings.add("%TO% isn`t that good, simply not human-sized enough");
		insultStrings.add("%TO% is a dip-shit");
		insultStrings.add("%TO% YOU LITTLE SHIT!");
	}

	@Override
	public void onJoin(JoinEvent<PircBotX> event) throws Exception {
		if (chatManager == null) {
			chatManager = new ChatManager(event.getChannel(), bot, this);
		}
		if (!event.getUser().getNick().equalsIgnoreCase(BOT_NAME)) bot.sendIRC().message(channelString, "Hello " + event.getUser().getNick());
		log("Channel joined");
		String message = "[" + event.getTimestamp() + "] JOIN: " + event.getChannel().getName() + "\n";
		log("Output made");
		fos.write(message.getBytes());
		log("Output outed");
		mailCheckEvent(event.getUser());
		log("Mail event triggered");
		super.onJoin(event);
		if (event.getUser().getNick().equals("VitBot")) {
			bot.sendIRC().message(channelString, "VitBot enabled");
		}
	}

	/**
	 * This will check the current mail status of the given user.
	 * 
	 * @param user
	 *            - The user to check mail for.
	 */
	public void mailCheckEvent(User user) {
		if (MailManager.hasMail(user.getNick())) {
			MailManager.sendMail(user);
		}
	}

	/**
	 * Items:<br>
	 * <ul>
	 * <li>ENABLE</li>
	 * <li>DISABLE</li>
	 * <li>RESET</li>
	 * <li>SET</li>
	 * <li>TERMINATE</li>
	 * <li>ADD</li>
	 * <li>REMOVE</li>
	 * <li>UPDATE</li>
	 * </ul>
	 * 
	 * @author Ryan
	 *
	 */
	enum SystemEvent {
		ENABLE, DISABLE, RESET, SET, TERMINATE, ADD, REMOVE, UPDATE;
	}

	@Override
	public void onPrivateMessage(PrivateMessageEvent<PircBotX> event) throws Exception {

		String message = event.getMessage();
		String[] spaceSplit = message.split("\\s+");
		if (spaceSplit[0].equals("!systems")) {
			switch (spaceSplit[1]) {
			case "update":
				chatManager.systemsProcessor(event.getUser());
				break;
			case "aio":
				event.respond("Operator status: " + (OperatorManager.isOperator(event.getUser().getNick()) ? Colors.GREEN + "Enabled" : Colors.RED + "Disabled"));
				break;
			default:
				break;
			}
			if (OperatorManager.isOperator(event.getUser().getNick())) {
				switch (spaceSplit[1]) {
				case "insult":
					if (spaceSplit[2].equals("add")) {
						String insult = message.split(startCharacter + "systems insult add")[1].substring(1);
						insultStrings.add(insult);
						event.respond("The insult '" + insult + "' has been added to the list. There are now " + insultStrings.size() + " insults.");
					}
					break;
				case "terminate":
					disableManager(false, event.getUser().getNick());
					break;
				case "karma":
					if (spaceSplit[2].equalsIgnoreCase("reset")) {
						karmaSystem(SystemEvent.RESET, message, event);
					}
					if (spaceSplit[2].equalsIgnoreCase("set")) {
						karmaSystem(SystemEvent.SET, message, event);
					}
					break;
				case "enable":
					enaDisSystem(SystemEvent.ENABLE, message, event);
					break;
				case "disable":
					enaDisSystem(SystemEvent.DISABLE, message, event);
					break;
				case "op":
					if (spaceSplit[2].equalsIgnoreCase("add")) {
						String nick = message.split("systems op add")[1].substring(1);
						OperatorManager.addOperator(nick);
						event.respond("User: " + nick + " has been " + Colors.GREEN + "added" + Colors.NORMAL + " to the operator list.");
						bot.sendIRC().message(nick, "You are now op");
					}
					if (spaceSplit[2].equalsIgnoreCase("remove")) {
						String nick = message.split("systems op add")[1].substring(1);
						OperatorManager.removeOperator(nick);
						event.respond("User: " + nick + " has been " + Colors.RED + "removed" + Colors.NORMAL + " to the operator list.");
						bot.sendIRC().message(nick, "You are no longer op");
					}
					break;
				case "mail":
					event.respond(chatManager.mailProcessor(message, event.getUser().getNick()));
				default:
					break;
				}
			}
		}
		super.onPrivateMessage(event);
	}

	/**
	 * This method handles all enabling and disabling of bot services.
	 * 
	 * @param event
	 *            - The {@link SystemEvent} that was called.
	 * @param message
	 *            - The message that was send to the bot
	 * @param pmEvent
	 *            - The private message event for responding.
	 */
	public void enaDisSystem(SystemEvent event, String message, PrivateMessageEvent<PircBotX> pmEvent) {
		String system = null;
		if (event == SystemEvent.DISABLE) system = message.split("disable")[1].substring(1);
		if (event == SystemEvent.ENABLE) system = message.split("enable")[1].substring(1);
		switch (system) {
		case "idiot":
			if (event == SystemEvent.DISABLE) {
				SystemsManager.disableIdiotSystem();
				pmEvent.respond("Idiot system has been " + Colors.RED + "disabled.");
			}
			if (event == SystemEvent.ENABLE) {
				SystemsManager.enableIdiotSystem();
				pmEvent.respond("Idiot system has been " + Colors.GREEN + "enabled.");
			}
			break;
		case "riot":
			if (event == SystemEvent.DISABLE) {
				SystemsManager.disableRiotSystem();
				pmEvent.respond("Riot system has been " + Colors.RED + "disabled.");
			}
			if (event == SystemEvent.ENABLE) {
				SystemsManager.enableRiotSystem();
				pmEvent.respond("Riot system has been " + Colors.GREEN + "enabled.");
			}
			break;
		case "lyric":
			if (event == SystemEvent.DISABLE) {
				SystemsManager.disableLyricSystem();
				pmEvent.respond("Lyric system has been " + Colors.RED + "disabled.");
			}
			if (event == SystemEvent.ENABLE) {
				SystemsManager.enableLyricSystem();
				pmEvent.respond("Lyric system has been " + Colors.GREEN + "enabled.");
			}
			break;
		case "norway":
			if (event == SystemEvent.DISABLE) {
				SystemsManager.disableNorwaySystem();
				pmEvent.respond("Norway system has been " + Colors.RED + "disabled.");
			}
			if (event == SystemEvent.ENABLE) {
				SystemsManager.enableNorwaySystem();
				pmEvent.respond("Norway system has been " + Colors.GREEN + "enabled.");
			}
			break;
		case "mail":
			if (event == SystemEvent.DISABLE) {
				SystemsManager.disableMailSystem();
				pmEvent.respond("Mail system has been " + Colors.RED + "disabled.");
			}
			if (event == SystemEvent.ENABLE) {
				SystemsManager.enableMailSystem();
				pmEvent.respond("Mail system has been " + Colors.GREEN + "enabled.");
			}
			break;
		case "karma":
			if (event == SystemEvent.DISABLE) {
				SystemsManager.disableKarmaSystem();
				pmEvent.respond("Karma system has been " + Colors.RED + "disabled.");
			}
			if (event == SystemEvent.ENABLE) {
				SystemsManager.enableKarmaSystem();
				pmEvent.respond("Karma system has been " + Colors.GREEN + "enabled.");
			}
			break;
		case "slap":
			if (event == SystemEvent.DISABLE) {
				SystemsManager.disableSlapSystem();
				pmEvent.respond("Slap system has been " + Colors.RED + "disabled.");
			}
			if (event == SystemEvent.ENABLE) {
				SystemsManager.enableSlapSystem();
				pmEvent.respond("Slap system has been " + Colors.GREEN + "enabled.");
			}
			break;
		case "party":
			if (event == SystemEvent.DISABLE) {
				SystemsManager.disablePartySystem();
				pmEvent.respond("Party system has been " + Colors.RED + "disabled.");
			}
			if (event == SystemEvent.ENABLE) {
				SystemsManager.enablePartySystem();
				pmEvent.respond("Party system has been " + Colors.GREEN + "enabled.");
			}
			break;
		default:
			pmEvent.respond("Unknows system.");
			break;
		}
		updateSystems();
	}

	public void updateSystems() {
		PersistentDataManager.saveSettingsToFile();
	}

	/**
	 * This method is in charge all all system commands relating to karma.
	 * 
	 * @param event
	 *            - The {@link SystemEvent} the was called.
	 * @param message
	 *            - The message sent to the bot.
	 * @param pmEvent
	 *            - The private message event for responding.
	 */
	public void karmaSystem(SystemEvent event, String message, PrivateMessageEvent<PircBotX> pmEvent) {
		if (event == SystemEvent.SET) {
			String[] split = message.split("\\s+");
			String user = split[3];
			String stringAmount = split[4];
			int amount;
			try {
				amount = Integer.parseInt(stringAmount);
				if (KarmaManager.hasKarmaAccount(user)) {
					KarmaManager.setKarma(user, amount);
				} else {
					KarmaUser karmaUser = new KarmaUser(user, amount);
					KarmaManager.addKarmaUser(karmaUser);
				}
				pmEvent.respond(user + "'s karma has been set to " + (amount > 0 ? Colors.GREEN : Colors.RED) + amount);
			} catch (NumberFormatException e) {
				pmEvent.respond("Use a valid number. Idiot.");
			}
		}
		if (event == SystemEvent.RESET) {
			String user = message.split("\\s+")[3];
			if (KarmaManager.hasKarmaAccount(user)) {
				KarmaManager.resetKarma(user);
			} else {
				KarmaUser kuser = new KarmaUser(user, 0);
				KarmaManager.addKarmaUser(kuser);
			}
		}
	}

	/**
	 * This method will disable all the features of the bot and try to shut it
	 * down.
	 * 
	 * @param maintain
	 *            - If the bot is being shut down for maintenance.
	 */
	public void disableManager(boolean maintain, String sender) {
		bot.sendIRC().message(channelString, maintain ? "VitBot disabled by " + sender + ": Down for maintenance" : "VitBot disabled by " + sender + ".");
		bot.stopBotReconnect();
		MailManager.saveAllMail();
		KarmaManager.saveAllKarma();
		System.exit(1);
	}

	@Override
	public void onMessage(MessageEvent<PircBotX> event) throws Exception {
		channel = event.getChannel();
		String message = "[" + event.getTimestamp() + "]" + event.getUser().getNick() + ": " + event.getMessage() + "\n";
		log("onGenericMessage: Message formed");
		fos.write(message.getBytes());
		log("onGenericMessage: Written");
		String response = respondToMessage(event.getMessage(), event.getUser().getNick(), event);
		log("onGenericMessage: Resonse generated.");
		if (response != null) {
			bot.sendIRC().message(channelString, response);
			log("onGenericMessage: Message sent");
		}
		mailCheckEvent(event.getUser());
		super.onMessage(event);
	}

	/**
	 * This is the processing center of the bot. This will determine the content
	 * of the message and then call the needed method.
	 * 
	 * @param message
	 *            - The message that was sent.
	 * @param nick
	 *            - The nickname of the person who sent the message.
	 * @param event
	 *            - The message event.
	 * @return String - the message to say back into chat.
	 */
	public String respondToMessage(String message, String nick, GenericMessageEvent<PircBotX> event) {
		log("Message: " + message);
		log("Nick: " + nick);
		if (message.toLowerCase().contains("norway")) {
			return "http://www.oljefondet.no/ NORWAY!";
		}
		if (message.toLowerCase().startsWith(startCharacter + "idiot")) {
			return chatManager.idiotProcessor(nick, message);
		}
		if (message.toLowerCase().startsWith(startCharacter + "vitbot")) {
			// return suggestionProcessor(nick, message);
		}
		if (message.toLowerCase().startsWith(startCharacter + "lyric")) {
			return chatManager.lyricProcessor(nick);
		}
		if (message.toLowerCase().contains(startCharacter + "riot")) {
			return chatManager.riotProcessor(nick);
		}
		if (message.toLowerCase().startsWith(startCharacter + "systems")) {
			chatManager.systemsProcessor(event.getUser());
		}
		if (message.toLowerCase().startsWith(startCharacter + "mail")) {
			return chatManager.mailProcessor(message, nick);
		}
		if (message.toLowerCase().startsWith(startCharacter + "++")) {
			return chatManager.karmaProcessor(true, event.getUser(), message);
		}
		if (message.toLowerCase().startsWith(startCharacter + "--")) {
			return chatManager.karmaProcessor(false, event.getUser(), message);
		}
		if (message.toLowerCase().startsWith(startCharacter + "??")) {
			return chatManager.karmaLookupProcessor(message);
		}
		if (message.toLowerCase().startsWith(startCharacter + "in")) {
			bot.sendIRC().message(channelString, chatManager.insultProcessor(nick, message));
		}
		if (message.toLowerCase().contains("hello vitbot") || message.toLowerCase().contains("sup vitbot")) {
			return "Hello, " + nick + ".";
		}
		if (message.toLowerCase().startsWith(startCharacter + "party")) {
			return chatManager.partyProcessor(message, event.getUser());
		}
		if (message.toLowerCase().startsWith(startCharacter + "help")) {
			for (String s : helpStrings) {
				bot.sendIRC().message(nick, s);
			}
		}
		if (message.toLowerCase().contains(startCharacter + "slap")) {
			return chatManager.slapProcessor(message, nick);
		}
		return null;
	}

	/**
	 * This method sends a message to the current channel.
	 * 
	 * @param message
	 *            - The message to say
	 */
	public void sendMessageToChannel(String message) {
		bot.sendIRC().message(channelString, message);
	}

	/**
	 * This method sends a message to the given user.
	 * 
	 * @param user
	 *            - The users nick
	 * @param message
	 *            - The message.
	 */
	public void sendMessageToUser(String user, String message) {
		bot.sendIRC().message(user, message);
	}

	/**
	 * This will return whether the bot is currently connected.
	 * 
	 * @return If the bot is connected.
	 */
	public boolean isConnected() {
		if (bot != null) {
			return bot.isConnected();
		} else {
			return false;
		}
	}

}
