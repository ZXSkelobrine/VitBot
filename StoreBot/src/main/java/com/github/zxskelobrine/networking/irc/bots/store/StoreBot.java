package com.github.zxskelobrine.networking.irc.bots.store;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

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

import com.github.zxskelobrine.networking.irc.bots.store.managers.MailManager;
import com.github.zxskelobrine.networking.irc.bots.store.managers.OperatorManager;
import com.github.zxskelobrine.networking.irc.bots.store.managers.PersistentDataManager;
import com.github.zxskelobrine.networking.irc.bots.store.managers.RiotControl;
import com.github.zxskelobrine.networking.irc.bots.store.managers.SystemsManager;
import com.google.common.collect.ImmutableList;

public class StoreBot extends ListenerAdapter<PircBotX> {

	private FileOutputStream fos;
	private FileOutputStream suggestionStream;

	private File output;

	private static PircBotX bot;

	public static final String BOT_NAME = "VitBot";
	private static final String BOT_IP_MASK = "VitBot";
	private static String hostname;
	public static String channelString;

	private String[] unchartedLyrics = new String[] { "Du", "Du", "Du", "Doo", "Du", "Dududoo", "du", "du", "du", "doo", "dududoo", "Du", "Du", "Dooooo", "do do do", "du do do", "doooooo", "duuuu dooooo", "dooooooooOOOOOOOOooooooo", "DuuuuuuuUUUUUUuuuuuuuuuu", "DoooooooouuuuuuuoooOOOUU", "DO DO DO DO DO DO DO do do", "du du du du du du du du", "DO DO", "(rumpa bum bum)", "bum bum bum" };
	private String[] riotStrings = new String[] { "%NICK% waves their arms like a retarted cow", "%NICK% slaps %RANDNICK% with a %SIZE% trout" };
	//	private String[] insultStrings = new String[]{"%TO%'s mum is a pinecone"}

	private RiotControl control;

	private Channel channel;
	private ImmutableList<User> users;

	private Random random = new Random();
	private String startCharacter = "~";

	private static void log(String message) {
		boolean isThePopeACatholic = true;
		if (!isThePopeACatholic) {
			System.out.println(message);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws IOException {
		if (args.length >= 2) {
			hostname = args[0];
			log("Set hostnae");
			channelString = args[1];
			log("Set channel");
			StoreBot sb = new StoreBot();
			log("Storebot created");
			log("Creating config");
			Configuration<PircBotX> configuration;
			try {
				configuration = new Configuration.Builder().setName(BOT_NAME).setLogin(BOT_IP_MASK).setAutoNickChange(false).setCapEnabled(true).addCapHandler(new TLSCapHandler(new UtilSSLSocketFactory().trustAllCertificates(), true)).addListener(sb).setServerHostname(hostname)// irc.quakenet.org
						.addAutoJoinChannel(channelString).buildConfiguration();//
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

	public StoreBot() throws IOException {
		control = new RiotControl();
		control.startRiotControl();
		File suggestionFile = new File("E:\\IRC Logs\\Bot Store\\Quakenet\\Project Awesome\\VitBot\\Suggestions\\Suggestions - " + new SimpleDateFormat("dd-MM-yy - hh").format(Calendar.getInstance().getTime()) + ".txt");//TODO Change to sef gen path.
		if (!suggestionFile.exists()) suggestionFile.createNewFile();
		output = new File("E:\\IRC Logs\\Bot Store\\Quakenet\\Project Awesome\\VitBot\\" + new SimpleDateFormat("dd-MM-yy - hh").format(Calendar.getInstance().getTime()) + ".txt");//TODO Change to sef gen path.
		suggestionStream = new FileOutputStream(suggestionFile, true);
		log("Output file set");
		if (!output.exists()) output.createNewFile();
		log("Output file created if needed");
		fos = new FileOutputStream(output, true);
		log("Output stream set");
	}

	@Override
	public void onJoin(JoinEvent<PircBotX> event) throws Exception {
		log("Channel joined");
		String message = "[" + event.getTimestamp() + "] JOIN: " + event.getChannel().getName() + "\n";
		log("Output made");
		fos.write(message.getBytes());
		log("Output outed");
		mailCheckEvent(event.getUser());
		log("Mail event triggered");
		super.onJoin(event);
	}

	public void mailCheckEvent(User user) {
		if (MailManager.hasMail(user.getNick())) {
			MailManager.sendMail(user);
		}
	}

	@Override
	public void onPrivateMessage(PrivateMessageEvent<PircBotX> event) throws Exception {

		String message = event.getMessage();
		if (message.startsWith(startCharacter + "systems update")) {
			systemsProcessor(event.getUser());
		}
		if (message.startsWith(startCharacter + "systems aio")) {
			event.respond("Operator status: " + (OperatorManager.isOperator(event.getUser().getNick()) ? Colors.GREEN + "Enabled" : Colors.RED + "Disabled"));
		}
		if (OperatorManager.isOperator(event.getUser().getNick())) {
			if (message.startsWith(startCharacter + "terminate")) {
				disableManager();

			}
			if (message.startsWith(startCharacter + "systems enable")) {
				System.out.println("System: " + message.split("enable")[1].substring(1));
				String system = message.split("enable")[1].substring(1);
				switch (system) {
				case "idiot":
					SystemsManager.enableIdiotSystem();
					event.respond("Idiot system has been " + Colors.GREEN + "enabled.");
					break;
				case "riot":
					SystemsManager.enableRiotSystem();
					event.respond("Riot system has been " + Colors.GREEN + "enabled.");
					break;
				case "lyric":
					SystemsManager.enableLyricSystem();
					event.respond("Lyric system has been " + Colors.GREEN + "enabled.");
					break;
				case "norway":
					SystemsManager.enableNorwaySystem();
					event.respond("Norway system has been " + Colors.GREEN + "enabled.");
					break;
				default:
					event.respond("Unknows system.");
					break;
				}
			}
			if (message.startsWith(startCharacter + "systems disable")) {
				System.out.println("System: " + message.split("disable")[1].substring(1));
				String system = message.split("disable")[1].substring(1);
				switch (system) {
				case "idiot":
					SystemsManager.disableIdiotSystem();
					event.respond("Idiot system has been " + Colors.RED + "disabled.");
					break;
				case "riot":
					SystemsManager.disableRiotSystem();
					event.respond("Riot system has been " + Colors.RED + "disabled.");
					break;
				case "lyric":
					SystemsManager.disableLyricSystem();
					event.respond("Lyric system has been " + Colors.RED + "disabled.");
					break;
				case "norway":
					SystemsManager.disableNorwaySystem();
					event.respond("Norway system has been " + Colors.RED + "disabled.");
					break;
				default:
					event.respond("Unknows system.");
					break;
				}
			}
			if (message.startsWith(startCharacter + "systems op add")) {
				String nick = message.split("systems op add")[1].substring(1);
				OperatorManager.addOperator(nick);
				event.respond("User: " + nick + " has been " + Colors.GREEN + "added" + Colors.NORMAL + " to the operator list.");
			}
			if (message.startsWith(startCharacter + "systems op remove")) {
				String nick = message.split("systems op add")[1].substring(1);
				OperatorManager.removeOperator(nick);
				event.respond("User: " + nick + " has been " + Colors.RED + "removed" + Colors.NORMAL + " to the operator list.");
			}
			if (message.toLowerCase().startsWith(startCharacter + "mail")) {
				event.respond(mailProcessor(message, event.getUser().getNick()));
			}
		}
		super.onPrivateMessage(event);
	}

	private void disableManager() {
		bot.sendIRC().message(channelString, "VitBot disabled.");
		MailManager.saveAllMail();
		System.exit(1);
	}

	@Override
	public void onMessage(MessageEvent<PircBotX> event) throws Exception {
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

	@Override
	public void onGenericMessage(GenericMessageEvent<PircBotX> event) throws Exception {
		log("Generic");
		super.onGenericMessage(event);
	}

	public String respondToMessage(String message, String nick, GenericMessageEvent<PircBotX> event) {
		log("Message: " + message);
		log("Nick: " + nick);
		if (message.toLowerCase().contains("norway")) {
			return "http://www.oljefondet.no/ NORWAY!";
		}
		if (message.toLowerCase().startsWith(startCharacter + "idiot")) {
			return idiotProcessor(message);
		}
		if (message.toLowerCase().startsWith(startCharacter + "vitbot")) {
			return suggestionProcessor(nick, message);
		}
		if (message.toLowerCase().startsWith(startCharacter + "lyric")) {
			return lyricProcessor(nick);
		}
		if (message.toLowerCase().contains(startCharacter + "riot")) {
			return riotProcessor(nick);
		}
		if (message.toLowerCase().startsWith(startCharacter + "systems")) {
			systemsProcessor(event.getUser());
		}
		if (message.toLowerCase().startsWith(startCharacter + "mail")) {
			return mailProcessor(message, nick);
		}
		return null;
	}

	private String mailProcessor(String message, String nick) {
		if (SystemsManager.isMailSystemEnabled()) {
			String[] spaceSplit = message.split("\\s+");
			String to = spaceSplit[1];
			StringBuilder sb = new StringBuilder();
			for (int i = 2; i < spaceSplit.length; i++) {
				sb.append(spaceSplit[i] + " ");
			}
			String mail = sb.toString();
			mail = mail.substring(0, mail.length() - 1);
			MailItem item = new MailItem(nick, mail, to);
			MailManager.addMail(item);
			return "Your message has been sent successfully " + nick;
		}
		return null;
	}

	private void systemsProcessor(User user) {
		user.send().message("Current systems status: ");
		user.send().message("Idiot: " + (SystemsManager.isIdiotSystemEnabled() ? Colors.GREEN + "Enabled" : Colors.RED + "Disabled"));
		user.send().message("Riot: " + (SystemsManager.isRiotSystemEnabled() ? Colors.GREEN + "Enabled" : Colors.RED + "Disabled"));
		user.send().message("Lyric: " + (SystemsManager.isLyricSystemEnabled() ? Colors.GREEN + "Enabled" : Colors.RED + "Disabled"));
		user.send().message("Norway: " + (SystemsManager.isNorwaySystemEnabled() ? Colors.GREEN + "Enabled" : Colors.RED + "Disabled"));
		user.send().message("Mail: " + (SystemsManager.isMailSystemEnabled() ? Colors.GREEN + "Enabled" : Colors.RED + "Disabled"));
	}

	private String lyricProcessor(String nick) {
		if (SystemsManager.isLyricSystemEnabled()) {
			return nick + ": " + unchartedLyrics[random.nextInt(unchartedLyrics.length)];
		}
		return null;
	}

	private String riotProcessor(String nick) {
		if (SystemsManager.isRiotSystemEnabled()) {
			control.addRiotUser(nick);
			int rioters = control.getRioters();
			bot.sendIRC().message(channelString, rioters == 1 ? "1 person is currently rioting." : rioters + " peope are currently rioting.");
			String riotMessage = riotStrings[random.nextInt(riotStrings.length)];
			riotMessage = riotMessage.replace("%NICK%", nick);
			if (riotMessage.contains("%RANDNICK%")) {
				riotMessage = riotMessage.replace("%RANDNICK%", users.get(random.nextInt(users.size())).getNick());
			}
			if (riotMessage.contains("%SIZE%")) {
				switch (random.nextInt(3)) {
				case 0:
					riotMessage = riotMessage.replace("%SIZE%", "small");
					break;
				case 1:
					riotMessage = riotMessage.replace("%SIZE%", "medium");
					break;
				case 2:
					riotMessage = riotMessage.replace("%SIZE%", "large");
					break;
				}
			}
			System.out.println(riotMessage);
			return riotMessage;
		}
		return null;
	}

	private String suggestionProcessor(String nick, String message) {
		String[] spaceSplit = message.split("\\s+");
		if (spaceSplit[1].equalsIgnoreCase("suggestion")) {
			try {
				suggestionStream.write((nick + ": " + message + "\n").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "Thank you " + nick + " for your suggestion.";
	}

	private String idiotProcessor(String message) {
		if (SystemsManager.isIdiotSystemEnabled()) {
			String[] spaceSplit = message.split("\\s+");
			String username = spaceSplit[1];
			String reason = "";
			if (spaceSplit.length > 2) {
				StringBuilder reasonBuilder = new StringBuilder();
				for (int i = 2; i < spaceSplit.length; i++) {
					reasonBuilder.append(spaceSplit[i] + " ");
				}
				reason = " because " + reasonBuilder.toString() + ".";
			} else if (spaceSplit.length < 2) {
				reason = ".";
			}

			return username + " you are an idiot" + reason;
		}
		return null;
	}

	public void updateChannelAndUsers(User user) {
		boolean updateUsers = false;
		for (Channel c : user.getChannels()) {
			if (c.getName().equals(channel.getName())) {
				channel = c;
				updateUsers = true;
			}
		}
		if (updateUsers) {
			users = channel.getUsers().asList();
		}
	}

}
