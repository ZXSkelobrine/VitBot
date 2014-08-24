package com.github.zxskelobrine.networking.irc.bots.store.systems.internal.slap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class SlapManager {

	public static final int MAX_HEALTH = 50;
	public static final int DELAY_MILLIS = 60 * 1000;

	private static List<SlapUser> users = new ArrayList<SlapUser>();
	private static List<String> names = new ArrayList<String>();

	private static Random random = new Random();

	public static void addSlapUser(SlapUser slapped) {
		users.add(slapped);
		names.add(slapped.getNick());
	}

	public static int slap(String nick) {
		List<String> names = getUserNames();
		int index = names.indexOf(nick);
		if (index != -1) {
			List<SlapUser> users = getUsers();
			SlapUser su = users.get(index);
			int damage = random.nextInt(10) + 1;
			su.damage(damage);
			SlapManager.users.set(index, su);
			return damage;
		}
		return -1;
	}

	public static int heal(String nick) {
		List<String> names = getUserNames();
		int index = names.indexOf(nick);
		if (index != -1) {
			List<SlapUser> users = getUsers();
			SlapUser su = users.get(index);
			int damage = random.nextInt(10) + 1;
			su.heal(damage);
			SlapManager.users.set(index, su);
			return damage;
		}
		return -1;
	}

	public static String getHealth(String nick) {
		List<String> names = getUserNames();
		int index = names.indexOf(nick);
		if (index != -1) {
			List<SlapUser> users = getUsers();
			SlapUser su = users.get(index);
			return su.getHealth();
		}
		return null;
	}

	public static List<SlapUser> getUsers() {
		return new CopyOnWriteArrayList<>(users);
	}

	public static List<String> getUserNames() {
		return new CopyOnWriteArrayList<>(names);
	}

	public static boolean hasSlapAccount(String nick) {
		return getUserNames().indexOf(nick) != -1;
	}

	public static SlapUser getSlapUser(String nick) {
		int index = getUserNames().indexOf(nick);
		if (index == -1) {
			SlapUser karmaUser = new SlapUser(MAX_HEALTH, nick);
			SlapManager.addSlapUser(karmaUser);
		}
		index = getUserNames().indexOf(nick);
		return getUsers().get(index);
	}

}
