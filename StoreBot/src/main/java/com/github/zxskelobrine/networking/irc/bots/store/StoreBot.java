package com.github.zxskelobrine.networking.irc.bots.store;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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

import com.github.zxskelobrine.networking.irc.bots.store.managers.ChatManager;
import com.github.zxskelobrine.networking.irc.bots.store.managers.OperatorManager;
import com.github.zxskelobrine.networking.irc.bots.store.managers.PersistentDataManager;
import com.github.zxskelobrine.networking.irc.bots.store.systems.SystemsManager;
import com.github.zxskelobrine.networking.irc.bots.store.systems.karma.KarmaManager;
import com.github.zxskelobrine.networking.irc.bots.store.systems.karma.KarmaUser;
import com.github.zxskelobrine.networking.irc.bots.store.systems.mail.MailManager;
import com.github.zxskelobrine.networking.irc.bots.store.systems.riot.RiotControl;
import com.google.common.collect.ImmutableList;

public class StoreBot extends ListenerAdapter<PircBotX> {

	private FileOutputStream fos;
	@SuppressWarnings("unused")
	private FileOutputStream suggestionStream;

	private File output;

	private static PircBotX bot;

	public static final String BOT_NAME = "VitBot";
	private static final String BOT_IP_MASK = "VitBot";

	private static String hostname;
	public static String channelString;
	public static final String startCharacter = "!";

	public static List<String> insultStrings = new ArrayList<String>();//new String[] { "%TO%'s mum is a pinecone.", "Shut up %TO% you goat in a waistcoat.", "%TO% you are Adolf Titler.", "%TO% you are a pavian.", "%TO% isn`t that good, simply not human-sized enough", "%TO% is a dip-shit", "%TO% YOU LITTLE SHIT!", "" };
	private String[] helpStrings = new String[] { "VitBot Help: ", StoreBot.startCharacter + "help: Prints this message", StoreBot.startCharacter + "mail <To> <Message>: Will send a message to <To> and will be delivered when they join or say a message.", StoreBot.startCharacter + "-- <User>: Subtracts one karma from <User>", StoreBot.startCharacter + "++ <User>: Adds one karma to <User>", StoreBot.startCharacter + "?? <User>: Shows the current karma of <User>", StoreBot.startCharacter + "lyric: Prints a stupid lyric", StoreBot.startCharacter + "riot: Shows how many people are rioting and adds you to the current riot." };

	private Channel channel;
	//	private ImmutableList<User> users;
	private ChatManager chatManager;

	//	private Random random = new Random();

	private static void log(String message) {
		boolean isThePopeACatholic = true;
		if (!isThePopeACatholic) {
			System.out.println(message);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args, StoreBot sbot) throws IOException {
		if (args.length >= 2) {
			hostname = args[0];
			log("Set hostnae");
			channelString = args[1];
			log("Set channel");
			log("Creating config");
			Configuration<PircBotX> configuration;
			try {
				configuration = new Configuration.Builder().setName(BOT_NAME).setLogin(BOT_IP_MASK).setAutoNickChange(false).setCapEnabled(true).addCapHandler(new TLSCapHandler(new UtilSSLSocketFactory().trustAllCertificates(), true)).addListener(sbot).setServerHostname(hostname)// irc.quakenet.org
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
		File suggestionFile = new File("E:\\IRC Logs\\Bot Store\\Quakenet\\Project Awesome\\VitBot\\Suggestions\\Suggestions - " + new SimpleDateFormat("dd-MM-yy - hh").format(Calendar.getInstance().getTime()) + ".txt");//TODO Change to sef gen path.
		if (!suggestionFile.exists()) suggestionFile.createNewFile();
		output = new File("E:\\IRC Logs\\Bot Store\\Quakenet\\Project Awesome\\VitBot\\" + new SimpleDateFormat("dd-MM-yy - hh").format(Calendar.getInstance().getTime()) + ".txt");//TODO Change to sef gen path.
		suggestionStream = new FileOutputStream(suggestionFile, true);
		log("Output file set");
		if (!output.exists()) output.createNewFile();
		log("Output file created if needed");
		fos = new FileOutputStream(output, true);
		log("Output stream set");
		prepareInsultStrings();
	}

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
			chatManager = new ChatManager(bot.getUserBot());
		}
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

	public void mailCheckEvent(User user) {
		if (MailManager.hasMail(user.getNick())) {
			MailManager.sendMail(user);
		}
	}

	@Override
	public void onPrivateMessage(PrivateMessageEvent<PircBotX> event) throws Exception {

		String message = event.getMessage();
		if (message.startsWith(startCharacter + "systems update")) {
			chatManager.systemsProcessor(event.getUser());
		}
		if (message.startsWith(startCharacter + "systems aio")) {
			event.respond("Operator status: " + (OperatorManager.isOperator(event.getUser().getNick()) ? Colors.GREEN + "Enabled" : Colors.RED + "Disabled"));
		}
		if (OperatorManager.isOperator(event.getUser().getNick())) {
			if (message.startsWith(startCharacter + "systems insult add")) {
				String insult = message.split(startCharacter + "systems insult add")[1].substring(1);
				insultStrings.add(insult);
				event.respond("The insult '" + insult + "' has been added to the list. There are now " + insultStrings.size() + " insults.");
			}
			if (message.startsWith(startCharacter + "terminate")) {
				disableManager();
			}
			if (message.startsWith(startCharacter + "systems karma reset")) {
				String user = message.split("\\s+")[3];
				if (KarmaManager.hasKarmaAccount(user)) {
					KarmaManager.resetKarma(user);
				} else {
					KarmaUser kuser = new KarmaUser(user, 0);
					KarmaManager.addKarmaUser(kuser);
				}
			}
			if (message.startsWith(startCharacter + "systems karma set")) {
				System.out.println("Done");
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
					event.respond(user + "'s karma has been set to " + (amount > 0 ? Colors.GREEN : Colors.RED) + amount);
				} catch (NumberFormatException e) {
					event.respond("Use a valid number. Idiot.");
				}
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
				case "mail":
					event.respond("Mail system has been " + Colors.GREEN + "enabled.");
					SystemsManager.enableMailSystem();
					break;
				case "karma":
					event.respond("Karma system has been " + Colors.GREEN + "enabled.");
					SystemsManager.enableKarmaSystem();
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
				case "mail":
					event.respond("Mail system has been " + Colors.RED + "disabled.");
					SystemsManager.disableMailSystem();
					break;
				case "karma":
					event.respond("Karma system has been " + Colors.RED + "disabled.");
					SystemsManager.disableKarmaSystem();
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
				event.respond(chatManager.mailProcessor(message, event.getUser().getNick()));
			}
		}
		super.onPrivateMessage(event);
	}

	public void disableManager() {
		bot.sendIRC().message(channelString, "VitBot disabled.");
		bot.stopBotReconnect();
		MailManager.saveAllMail();
		KarmaManager.saveAllKarma();
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

	public String respondToMessage(String message, String nick, GenericMessageEvent<PircBotX> event) {
		log("Message: " + message);
		log("Nick: " + nick);
		if (message.toLowerCase().contains("norway")) {
			return "http://www.oljefondet.no/ NORWAY!";
		}
		if (message.toLowerCase().startsWith(startCharacter + "idiot")) {
			return chatManager.idiotProcessor(message);
		}
		if (message.toLowerCase().startsWith(startCharacter + "vitbot")) {
			//			return suggestionProcessor(nick, message);
		}
		if (message.toLowerCase().startsWith(startCharacter + "lyric")) {
			return chatManager.lyricProcessor(nick);
		}
		if (message.toLowerCase().contains(startCharacter + "riot")) {
			return riotProcessor(nick);
		}
		if (message.toLowerCase().startsWith(startCharacter + "systems")) {
			chatManager.systemsProcessor(event.getUser());
		}
		if (message.toLowerCase().startsWith(startCharacter + "mail")) {
			return chatManager.mailProcessor(message, nick);
		}
		if (message.toLowerCase().startsWith(startCharacter + "++")) {
			return chatManager.karmaProcessor(true, nick, message);
		}
		if (message.toLowerCase().startsWith(startCharacter + "--")) {
			return chatManager.karmaProcessor(false, nick, message);
		}
		if (message.toLowerCase().startsWith(startCharacter + "??")) {
			return chatManager.karmaLookupProcessor(message);
		}
		if (message.toLowerCase().startsWith(startCharacter + "in")) {
			bot.sendIRC().message(channelString, chatManager.insultProcessor(message));
		}
		if(message.toLowerCase().startsWith(startCharacter + "terminate")){
			if(OperatorManager.isOperator(nick)){
				disableManager();
			}
		}
		if (message.toLowerCase().startsWith(startCharacter + "help")) {
			for (String s : helpStrings) {
				bot.sendIRC().message(nick, s);
			}
		}
		return null;
	}

	public String riotProcessor(String nick) {
		if (SystemsManager.isRiotSystemEnabled()) {
			chatManager.control.addRiotUser(nick);
			int rioters = chatManager.control.getRioters();
			bot.sendIRC().message(channelString, rioters == 0 ? "1 person is currently rioting." : (rioters == 1 ? "1 person is currently rioting." : rioters + " peope are currently rioting."));
			String riotMessage = chatManager.riotStrings[chatManager.random.nextInt(chatManager.riotStrings.length)];
			riotMessage = riotMessage.replace("%NICK%", nick);
			if (riotMessage.contains("%RANDNICK%")) {
				riotMessage = riotMessage.replace("%RANDNICK%", chatManager.users.get(chatManager.random.nextInt(chatManager.users.size())).getNick());
			}
			if (riotMessage.contains("%SIZE%")) {
				switch (chatManager.random.nextInt(3)) {
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
			//			bot.sendIRC().message(channelString, riotMessage);
			return riotMessage;
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
			//			users = channel.getUsers().asList();
		}
	}

}
