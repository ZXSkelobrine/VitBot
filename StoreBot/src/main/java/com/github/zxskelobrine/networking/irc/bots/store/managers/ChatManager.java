package com.github.zxskelobrine.networking.irc.bots.store.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.User;

import com.github.zxskelobrine.networking.irc.bots.store.StoreBot;
import com.github.zxskelobrine.networking.irc.bots.store.systems.SystemsManager;
import com.github.zxskelobrine.networking.irc.bots.store.systems.karma.KarmaManager;
import com.github.zxskelobrine.networking.irc.bots.store.systems.karma.KarmaUser;
import com.github.zxskelobrine.networking.irc.bots.store.systems.mail.MailItem;
import com.github.zxskelobrine.networking.irc.bots.store.systems.mail.MailManager;
import com.github.zxskelobrine.networking.irc.bots.store.systems.riot.RiotControl;

public class ChatManager {

	private String[] unchartedLyrics = new String[] { "Du", "Du", "Du", "Doo", "Du", "Dududoo", "du", "du", "du", "doo", "dududoo", "Du", "Du", "Dooooo", "do do do", "du do do", "doooooo", "duuuu dooooo", "dooooooooOOOOOOOOooooooo", "DuuuuuuuUUUUUUuuuuuuuuuu", "DoooooooouuuuuuuoooOOOUU", "DO DO DO DO DO DO DO do do", "du du du du du du du du", "DO DO", "(rumpa bum bum)", "bum bum bum" };
	public String[] riotStrings = new String[] { "%NICK% waves their arms like a retarted cow", "%NICK% slaps %RANDNICK% with a %SIZE% trout" };
	public Random random = new Random();
	public RiotControl control;
	public List<User> users = new ArrayList<User>();

	public ChatManager(final User user) {
		control = new RiotControl();
		new Thread() {
			public void run() {
				for (Channel c : user.getChannels()) {
					if (c.getName().equals(StoreBot.channelString)) {
						users = c.getUsers().asList();
						System.out.println("User list created.");
					}
				}
			};
		}.start();
	}

	public String karmaLookupProcessor(String message) {
		if (SystemsManager.isKarmaSystemEnabled()) {
			String user = message.split("\\s+")[1];
			if (KarmaManager.hasKarmaAccount(user)) {
				return user + " has " + KarmaManager.getKarmaAmount(user) + " karma.";
			} else {
				KarmaUser karmaUser = new KarmaUser(user, 0);
				KarmaManager.addKarmaUser(karmaUser);
				return user + " has " + KarmaManager.getKarmaAmount(user) + " karma.";
			}
		}
		return null;
	}

	public String insultProcessor(String message) {
		if (SystemsManager.isInsultsSystemEnabled()) {
			String to = message.split("\\s+")[1];
			String insult = StoreBot.insultStrings.get(random.nextInt(StoreBot.insultStrings.size())).replace("%TO%", to);
			return insult;
		}
		return null;
	}

	public String karmaProcessor(boolean add, String nick, String message) {
		if (SystemsManager.isKarmaSystemEnabled()) {
			String[] spaceSplit = message.split("\\s+");
			String to = spaceSplit[1];
			if (nick.equalsIgnoreCase(to)) {
				if (KarmaManager.hasKarmaAccount(to)) {
					KarmaManager.removeKarma(to);
				} else {
					KarmaUser karmaUser = new KarmaUser(to, -1);
					KarmaManager.addKarmaUser(karmaUser);
				}
				return to + " don't tip yourself you goat in a waitcoat. " + to + " now has " + KarmaManager.getKarmaAmount(to) + " karma.";
			}
			if (KarmaManager.hasKarmaAccount(to)) {
				if (add) {
					KarmaManager.addKarma(to);
				} else {
					KarmaManager.removeKarma(to);
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
			return add ? nick + " has tipped " + to + " with one karma. " + to + " now has " + KarmaManager.getKarmaAmount(to) + " karma." : nick + " has removed one karma point from " + to + ". " + to + " now has " + KarmaManager.getKarmaAmount(to) + " karma.";
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
	}

	public String lyricProcessor(String nick) {
		if (SystemsManager.isLyricSystemEnabled()) {
			return nick + ": " + unchartedLyrics[random.nextInt(unchartedLyrics.length)];
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

	public String idiotProcessor(String message) {
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

}
