package com.github.zxskelobrine.networking.irc.bots.store.systems.karma;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.zxskelobrine.networking.irc.bots.store.managers.PersistentDataManager;

public class KarmaManager {
	private static List<KarmaUser> karma = new ArrayList<KarmaUser>();
	private static List<String> karmaStrings = new ArrayList<String>();

	public synchronized static void addKarmaUser(KarmaUser item) {
		synchronized (karma) {
			karma.add(item);
			karmaStrings.add(item.getNick());
		}
	}

	public synchronized static void addKarma(String nick) {
		synchronized (karma) {
			int index = karmaStrings.indexOf(nick);
			if (index != -1) {
				KarmaUser karmaUser = karma.get(index);
				karmaUser.incrementKarma();
				karma.set(index, karmaUser);
			}
		}
	}

	public synchronized static void removeKarma(String nick) {
		synchronized (karma) {
			int index = karmaStrings.indexOf(nick);
			if (index != -1) {
				KarmaUser karmaUser = karma.get(index);
				karmaUser.decrementKarma();
				karma.set(index, karmaUser);
			}
		}
	}

	public synchronized static void saveAllKarma() {
		synchronized (karma) {
			CopyOnWriteArrayList<KarmaUser> items = new CopyOnWriteArrayList<>(karma);
			for (KarmaUser item : items) {
				PersistentDataManager.storeKarma(item);
			}
		}
	}

	public static void loadKarmaFromPersistent() {
		karma = PersistentDataManager.getKarmaUsers();
		for (KarmaUser u : karma) {
			karmaStrings.add(u.getNick());
		}
	}

	public synchronized static boolean hasKarmaAccount(String nick) {
		synchronized (karmaStrings) {
			return karmaStrings.indexOf(nick) != -1;
		}
	}

	public synchronized static int getKarmaAmount(String nick) {
		synchronized (karma) {
			return karma.get(karmaStrings.indexOf(nick)).getKarma();
		}
	}

	public synchronized static void resetKarma(String nick) {
		synchronized (karma) {
			setKarma(nick, 0);
		}
	}

	public synchronized static void setKarma(String nick, int amount) {
		synchronized (karma) {
			int index = karmaStrings.indexOf(nick);
			if (index != -1) {
				KarmaUser karmaUser = karma.get(index);
				karmaUser.setKarma(amount);
				karma.set(index, karmaUser);
			}
		}
	}

}
