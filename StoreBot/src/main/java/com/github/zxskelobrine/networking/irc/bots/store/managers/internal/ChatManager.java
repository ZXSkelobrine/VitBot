package com.github.zxskelobrine.networking.irc.bots.store.managers.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

import com.github.zxskelobrine.networking.irc.bots.store.StoreBot;
import com.github.zxskelobrine.networking.irc.bots.store.systems.internal.SystemsManager;
import com.github.zxskelobrine.networking.irc.bots.store.systems.internal.karma.KarmaManager;
import com.github.zxskelobrine.networking.irc.bots.store.systems.internal.karma.KarmaUser;
import com.github.zxskelobrine.networking.irc.bots.store.systems.internal.mail.MailItem;
import com.github.zxskelobrine.networking.irc.bots.store.systems.internal.mail.MailManager;
import com.github.zxskelobrine.networking.irc.bots.store.systems.internal.riot.RiotManager;
import com.github.zxskelobrine.networking.irc.bots.store.systems.internal.slap.SlapManager;
import com.github.zxskelobrine.networking.irc.bots.store.systems.internal.slap.SlapUser;
import com.github.zxskelobrine.networking.irc.bots.store.systems.cooldown.CooldownTimerTask.CooldownType;

public class ChatManager {

	private String[] unchartedLyrics = new String[] { "Du", "Du", "Du", "Doo", "Du", "Dududoo", "du", "du", "du", "doo", "dududoo", "Du", "Du", "Dooooo", "do do do", "du do do", "doooooo", "duuuu dooooo", "dooooooooOOOOOOOOooooooo", "DuuuuuuuUUUUUUuuuuuuuuuu", "DoooooooouuuuuuuoooOOOUU", "DO DO DO DO DO DO DO do do", "du du du du du du du du", "DO DO", "(rumpa bum bum)", "bum bum bum" };
	public String[] riotStrings = new String[] { "%NICK% waves their arms like a retarted cow", "%NICK% slaps %RANDNICK% with a %SIZE% trout" };
	public String[] compliments = new String[] { "%TO%: You are cool!", "%TO% you are so human sized.", "%TO% I dislike you the least.", "%TO% on the list of people I want to kill, you are at the bottom.", "%TO%, You think of the funniest names for wi-fi networks.", "%TO% you're funny!", "%TO% - Your voice sounds like a thousand cats purring. (I may be on acid...)", "%TO% you're awesome for slaying the blue dragon.", "%TO% Your allergies are some of the least embarrassing allergies.", "%TO% You are better than everyone - even Gary", "%TO% You are freakishly good at thumb wars.", "%TO% I am going to name my goldfish after you!", "%TO% I want some of your hair!", "%TO% You are awesome becuase you are a kiwi.", "%TO% People behind you at movies think you are the perfect height.", "%TO%Sushi chefs are wowed by your chopstick dexterity." };
	public Random random = new Random();
	public RiotManager control;
	public List<User> users = new ArrayList<User>();
	public Channel channel;
	public PircBotX botX;
	public StoreBot bot;

	public ChatManager(Channel channel, PircBotX bot, StoreBot storeBot) {
		control = new RiotManager();
		this.botX = bot;
		this.users = channel.getUsers().asList();
		this.channel = channel;
		this.bot = storeBot;
	}

	/**
	 * This will look up the karma of the given user.
	 * 
	 * @param sender
	 *            - The person who sent the command.
	 * @param message
	 *            - The message they sent
	 * @return String - the string to say back.
	 */
	public String karmaLookupProcessor(String sender, String message) {
		//If the karma system is enabled.
		if (SystemsManager.isKarmaSystemEnabled()) {
			//Split the message every space.
			String[] spaces = message.split("\\s+");
			//If there are two words ([SC]?? <User>)
			if (spaces.length == 2) {
				//Lookup the karma of the given user.
				return lookupKarma(spaces[1]);
				//Otherwise if there is only one word ([SC]??)
			} else if (spaces.length == 1) {
				//Lookup the karma of the sender;
				return lookupKarma(sender);
				//Otherwise
			} else {
				//Return an invalid message.
				return "Invalid arguments.";
			}
		}
		return null;
	}

	/**
	 * This will return the karma of the given user in a readable form.
	 * 
	 * @param user
	 *            - The user to lookup.
	 * @return String - the readable karma.
	 */
	private String lookupKarma(String user) {
		if (KarmaManager.hasKarmaAccount(user)) {
			return user + " has " + KarmaManager.getKarmaAmount(user) + " karma.";
		} else {
			KarmaUser karmaUser = new KarmaUser(user, 0);
			KarmaManager.addKarmaUser(karmaUser);
			return user + " has " + KarmaManager.getKarmaAmount(user) + " karma.";
		}
	}

	public String insultProcessor(String sender, String message) {
		if (SystemsManager.isInsultsSystemEnabled()) {
			if (!CoolDownManager.cooldownTimerTask.hasCooldown(sender, CooldownType.INSULT)) {
				String to = message.split("\\s+")[1];
				String insult;
				if (bot.useInsultSwears) {
					if (random.nextInt(2) == 0) {
						insult = StoreBot.swearInsultStrings.get(random.nextInt(StoreBot.swearInsultStrings.size())).replace("%TO%", to);
					} else {
						insult = StoreBot.insultStrings.get(random.nextInt(StoreBot.insultStrings.size())).replace("%TO%", to);
					}
				} else {
					insult = StoreBot.insultStrings.get(random.nextInt(StoreBot.insultStrings.size())).replace("%TO%", to);
				}
				CoolDownManager.cooldownTimerTask.activateCooldown(sender, CooldownType.INSULT);
				return insult;
			} else {
				bot.sendMessageToUser(sender, "You cannot insult anyone - you must wait " + CoolDownManager.cooldownTimerTask.getTimeLeft(sender, CooldownType.INSULT) + " seconds.");
			}
		}
		return null;
	}

	public String karmaProcessor(boolean add, User nick, String message) {
		if (SystemsManager.isKarmaSystemEnabled()) {
			if (!CoolDownManager.cooldownTimerTask.hasCooldown(nick.getNick(), CooldownType.KARMA)) {
				String[] spaceSplit = message.split("\\s+");
				String to = spaceSplit[1];
				if (nick.getNick().equalsIgnoreCase(to)) {
					if (KarmaManager.hasKarmaAccount(to)) {
						KarmaManager.removeKarma(nick.getNick(), to);
					} else {
						KarmaUser karmaUser = new KarmaUser(to, -1);
						KarmaManager.addKarmaUser(karmaUser);
					}
					return to + " don't tip yourself you goat in a waitcoat. " + to + " now has " + KarmaManager.getKarmaAmount(to) + " karma.";
				}
				if (KarmaManager.hasKarmaAccount(to)) {
					if (add) {
						KarmaManager.addKarma(nick.getNick(), to);
					} else {
						KarmaManager.removeKarma(nick.getNick(), to);
					}
				} else {
					KarmaUser user;
					if (add) {
						user = new KarmaUser(to, 1);
					} else {
						user = new KarmaUser(to, -1);
					}
					KarmaManager.addKarmaUser(user);
				}
				CoolDownManager.cooldownTimerTask.activateCooldown(nick.getNick(), CooldownType.KARMA);
				return add ? nick.getNick() + " has tipped " + to + " with one karma. " + to + " now has " + KarmaManager.getKarmaAmount(to) + " karma." : nick + " has removed one karma point from " + to + ". " + to + " now has " + KarmaManager.getKarmaAmount(to) + " karma.";
			} else {
				bot.sendMessageToUser(nick.getNick(), "You cannot use karma - you must wait " + CoolDownManager.cooldownTimerTask.getTimeLeft(nick.getNick(), CooldownType.KARMA) + " seconds.");
			}
		}
		return null;
	}

	public String mailProcessor(String message, String nick) {
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

	public void systemsProcessor(User user) {
		user.send().message("Current systems status: ");
		user.send().message("Idiot: " + (SystemsManager.isIdiotSystemEnabled() ? Colors.GREEN + "Enabled" : Colors.RED + "Disabled"));
		user.send().message("Riot: " + (SystemsManager.isRiotSystemEnabled() ? Colors.GREEN + "Enabled" : Colors.RED + "Disabled"));
		user.send().message("Lyric: " + (SystemsManager.isLyricSystemEnabled() ? Colors.GREEN + "Enabled" : Colors.RED + "Disabled"));
		user.send().message("Norway: " + (SystemsManager.isNorwaySystemEnabled() ? Colors.GREEN + "Enabled" : Colors.RED + "Disabled"));
		user.send().message("Mail: " + (SystemsManager.isMailSystemEnabled() ? Colors.GREEN + "Enabled" : Colors.RED + "Disabled"));
		user.send().message("Karma: " + (SystemsManager.isKarmaSystemEnabled() ? Colors.GREEN + "Enabled" : Colors.RED + "Disabled"));
		user.send().message("Party: " + (SystemsManager.isPartySystemEnabled() ? Colors.GREEN + "Enabled" : Colors.RED + "Disabled"));
	}

	public String lyricProcessor(String nick) {
		if (SystemsManager.isLyricSystemEnabled()) {
			if (!CoolDownManager.cooldownTimerTask.hasCooldown(nick, CooldownType.LYRIC)) {
				CoolDownManager.cooldownTimerTask.activateCooldown(nick, CooldownType.LYRIC);
				return nick + ": " + unchartedLyrics[random.nextInt(unchartedLyrics.length)];
			} else {
				bot.sendMessageToUser(nick, "You cannot use use lyrics - you must wait " + CoolDownManager.cooldownTimerTask.getTimeLeft(nick, CooldownType.LYRIC) + " seconds.");
			}
		}
		return null;
	}

	/**
	 * <pre>
	 * public String suggestionProcessor(String nick, String message) {
	 * 	String[] spaceSplit = message.split(&quot;\\s+&quot;);
	 * 	if (spaceSplit[1].equalsIgnoreCase(&quot;suggestion&quot;)) {
	 * 		try {
	 * 			suggestionStream.write((nick + &quot;: &quot; + message + &quot;\n&quot;).getBytes());
	 * 		} catch (IOException e) {
	 * 			e.printStackTrace();
	 * 		}
	 * 	}
	 * 	return &quot;Thank you &quot; + nick + &quot; for your suggestion.&quot;;
	 * }
	 * </pre>
	 */

	public String idiotProcessor(String nick, String message) {
		if (SystemsManager.isIdiotSystemEnabled()) {
			if (!CoolDownManager.cooldownTimerTask.hasCooldown(nick, CooldownType.IDIOT)) {
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
				CoolDownManager.cooldownTimerTask.activateCooldown(nick, CooldownType.IDIOT);
				return username + " you are an idiot" + reason;
			}
		} else {
			bot.sendMessageToUser(nick, "You cannot call anyone an idiot - you must wait " + CoolDownManager.cooldownTimerTask.getTimeLeft(nick, CooldownType.IDIOT) + " seconds.");
		}
		return null;
	}

	public String slapProcessor(String message, String nick) {
		if (SystemsManager.isSlapSystemEnabled()) {
			int karma = KarmaManager.getKarmaAmount(nick);
			if (karma > KarmaManager.SLAP_TRHESHOLD) {
				String[] spaceSplit = message.split("\\s+");
				String username = spaceSplit[1];
				if (!CoolDownManager.cooldownTimerTask.hasCooldown(nick, CooldownType.SLAP)) {
					if (SlapManager.hasSlapAccount(username)) {
						int damage = SlapManager.slap(username);
						CoolDownManager.cooldownTimerTask.activateCooldown(nick, CooldownType.SLAP);
						if (damage != -1) {
							return nick + " slapped " + username + " for " + damage + " damage. " + username + " now has " + SlapManager.getHealth(username) + " health";
						}
					} else {
						SlapUser su = new SlapUser(SlapManager.MAX_HEALTH, username);
						SlapManager.addSlapUser(su);
						int damage = SlapManager.slap(username);
						CoolDownManager.cooldownTimerTask.activateCooldown(nick, CooldownType.SLAP);
						if (damage != -1) {
							return nick + " slapped " + username + " for " + damage + " damage. " + username + " now has " + SlapManager.getHealth(username) + " health";
						}
					}
				} else {
					bot.sendMessageToUser(nick, "You cannot slap - you must wait " + CoolDownManager.cooldownTimerTask.getTimeLeft(nick, CooldownType.SLAP) + " seconds.");
				}
			} else {
				return nick + " does not have enough karma to slap.";
			}
		}
		return null;
	}

	public String partyProcessor(String message, final User user) {
		if (SystemsManager.isPartySystemEnabled()) {
			int karma = KarmaManager.getKarmaAmount(user.getNick());
			if (karma >= KarmaManager.PARTY_THRESHOLD) {
				if (!CoolDownManager.cooldownTimerTask.hasCooldown(user.getNick(), CooldownType.PARTY)) {
					CoolDownManager.cooldownTimerTask.activateCooldown(user.getNick(), CooldownType.PARTY);
					new Thread() {
						public void run() {
							if (StoreBot.channel != null) {
								for (User u : StoreBot.channel.getUsers()) {
									if (KarmaManager.getKarmaAmount(u.getNick()) > 3) {
										try {
											Thread.sleep(3000);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										bot.sendMessageToChannel(getRandomColor(random.nextInt(20)) + compliments[random.nextInt(compliments.length)].replace("%TO%", u.getNick()));
									}
								}
							}
						};
					}.start();
				} else {
					bot.sendMessageToUser(user.getNick(), "You cannot party - you must wait " + CoolDownManager.cooldownTimerTask.getTimeLeft(user.getNick(), CooldownType.PARTY) + " seconds.");
				}
			} else {
				return user.getNick() + " does not have enough karma to party.";
			}
		}
		return null;
	}

	private String getRandomColor(int randomInt) {
		switch (randomInt) {
		case 0:
			return Colors.BLACK;
		case 1:
			return Colors.BLUE;
		case 2:
			return Colors.BOLD;
		case 3:
			return Colors.BROWN;
		case 4:
			return Colors.CYAN;
		case 5:
			return Colors.DARK_BLUE;
		case 6:
			return Colors.DARK_GRAY;
		case 7:
			return Colors.DARK_GREEN;
		case 8:
			return Colors.GREEN;
		case 9:
			return Colors.LIGHT_GRAY;
		case 10:
			return Colors.MAGENTA;
		case 11:
			return Colors.NORMAL;
		case 12:
			return Colors.OLIVE;
		case 13:
			return Colors.PURPLE;
		case 14:
			return Colors.RED;
		case 15:
			return Colors.REVERSE;
		case 16:
			return Colors.TEAL;
		case 17:
			return Colors.UNDERLINE;
		case 18:
			return Colors.WHITE;
		case 19:
			return Colors.YELLOW;
		default:
			return Colors.NORMAL;
		}
	}

	/**
	 * This method is in charge of handling all riot activities - Should be in
	 * {@link ChatManager} but does not work.
	 * 
	 * @param nick
	 *            - The nickname of the person running the command.
	 * @return String - the message to say in chat.
	 */
	public String riotProcessor(String nick) {
		if (SystemsManager.isRiotSystemEnabled()) {
			if (!CoolDownManager.cooldownTimerTask.hasCooldown(nick, CooldownType.RIOT)) {
				control.addRiotUser(nick);
				int rioters = control.getRioters();
				botX.sendIRC().message(channel.getName(), rioters == 0 ? "1 person is currently rioting." : (rioters == 1 ? "1 person is currently rioting." : rioters + " peope are currently rioting."));
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
				CoolDownManager.cooldownTimerTask.activateCooldown(nick, CooldownType.RIOT);
				return riotMessage;
			}
		} else {
			bot.sendMessageToUser(nick, "You cannot riot - you must wait " + CoolDownManager.cooldownTimerTask.getTimeLeft(nick, CooldownType.RIOT) + " seconds.");
		}
		return null;
	}
}
