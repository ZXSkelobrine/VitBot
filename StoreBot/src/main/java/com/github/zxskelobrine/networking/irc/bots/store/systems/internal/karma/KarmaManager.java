package com.github.zxskelobrine.networking.irc.bots.store.systems.internal.karma;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.zxskelobrine.networking.irc.bots.store.managers.internal.PersistentDataManager;

public class KarmaManager {

    public static final int SLAP_TRHESHOLD = 5;
    public static final int PARTY_THRESHOLD = 10;

    private static List<KarmaUser> karma = new ArrayList<KarmaUser>();
    private static List<String> karmaStrings = new ArrayList<String>();

    public synchronized static void addKarmaUser(KarmaUser item) {
	synchronized (karma) {
	    karma.add(item);
	    karmaStrings.add(item.getNick());
	}
    }

    public static void addKarma(String sender, String nick) {
	List<String> names = getKarmaStrings();
	int index = names.indexOf(nick);
	if (index != -1) {
	    List<KarmaUser> karma = getKarma();
	    KarmaUser karmaUser = karma.get(index);
	    karmaUser.incrementKarma();
	    karma.set(index, karmaUser);
	}
    }

    public static void removeKarma(String sender, String nick) {
	List<String> names = getKarmaStrings();
	int index = names.indexOf(nick);
	if (index != -1) {
	    List<KarmaUser> karma = getKarma();
	    KarmaUser karmaUser = karma.get(index);
	    karmaUser.decrementKarma();
	    karma.set(index, karmaUser);
	}
    }

    public static void saveAllKarma() {
	List<KarmaUser> items = getKarma();
	for (KarmaUser item : items) {
	    PersistentDataManager.storeKarma(item);
	}
    }

    public static void loadKarmaFromPersistent() {
	karma = PersistentDataManager.getKarmaUsers();
	for (KarmaUser u : karma) {
	    karmaStrings.add(u.getNick());
	}
    }

    public static boolean hasKarmaAccount(String nick) {
	return karmaStrings.indexOf(nick) != -1;
    }

    public static int getKarmaAmount(String nick) {
	int index = getKarmaStrings().indexOf(nick);
	if (index == -1) {
	    KarmaUser karmaUser = new KarmaUser(nick, 0);
	    KarmaManager.addKarmaUser(karmaUser);
	}
	index = getKarmaStrings().indexOf(nick);
	return getKarma().get(getKarmaStrings().indexOf(nick)).getKarma();
    }

    public synchronized static void resetKarma(String nick) {
	synchronized (karma) {
	    setKarma(nick, 0);
	}
    }

    public static void setKarma(String nick, int amount) {
	List<String> names = getKarmaStrings();
	int index = names.indexOf(nick);
	if (index != -1) {
	    List<KarmaUser> karma = getKarma();
	    KarmaUser karmaUser = karma.get(index);
	    karmaUser.setKarma(amount);
	    karma.set(index, karmaUser);
	}
    }

    private static List<KarmaUser> getKarma() {
	return new CopyOnWriteArrayList<>(karma);
    }

    private static List<String> getKarmaStrings() {
	return new CopyOnWriteArrayList<>(karmaStrings);
    }

}
