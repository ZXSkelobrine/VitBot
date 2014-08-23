package com.github.zxskelobrine.networking.irc.bots.store.systems.cooldown;

import java.util.HashMap;
import java.util.TimerTask;

public class CooldownTimerTask extends TimerTask {

	public enum CooldownType {
		KARMA, PARTY, SLAP, IDIOT, RIOT, LYRIC, INSULT;
	}

	private HashMap<String, HashMap<CooldownType, Long>> cooldowns = new HashMap<String, HashMap<CooldownType, Long>>();

	public int seconds = 60;

	public boolean hasCooldown(String nick, CooldownType type) {
		HashMap<CooldownType, Long> map = cooldowns.get(nick);
		if (map == null) {
			return false;
		}
		Long lon = map.get(type);
		if (lon == null) {
			return false;
		}
		return !(lon < (System.currentTimeMillis() - (seconds * 1000)));
	}

	public void activateCooldown(String nick, CooldownType type) {
		if (cooldowns.containsKey(nick)) {
			HashMap<CooldownType, Long> current = cooldowns.get(nick);
			current.put(type, System.currentTimeMillis());
			cooldowns.put(nick, current);
		} else {
			HashMap<CooldownType, Long> newMap = new HashMap<CooldownType, Long>();
			newMap.put(type, System.currentTimeMillis());
			cooldowns.put(nick, newMap);
		}
	}

	private void removeOneSecond(String nick, CooldownType type) {
		if (cooldowns.containsKey(nick)) {
			HashMap<CooldownType, Long> current = cooldowns.get(nick);
			current.put(type, current.get(type).longValue() - 1000);
			cooldowns.put(nick, current);
		}
	}

	public int getTimeLeft(String nick, CooldownType type) {
		Long lon = cooldowns.get(nick).get(type);
		if (lon == null) return 0;
		return (int) (((lon + (seconds * 1000)) - System.currentTimeMillis()) / 1000);

	}

	@Override
	public void run() {
		for (String string : cooldowns.keySet()) {
			if (hasCooldown(string, CooldownType.IDIOT)) removeOneSecond(string, CooldownType.IDIOT);
			if (hasCooldown(string, CooldownType.KARMA)) removeOneSecond(string, CooldownType.KARMA);
			if (hasCooldown(string, CooldownType.PARTY)) removeOneSecond(string, CooldownType.PARTY);
			if (hasCooldown(string, CooldownType.RIOT)) removeOneSecond(string, CooldownType.RIOT);
			if (hasCooldown(string, CooldownType.SLAP)) removeOneSecond(string, CooldownType.SLAP);
			if (hasCooldown(string, CooldownType.LYRIC)) removeOneSecond(string, CooldownType.LYRIC);
			if (hasCooldown(string, CooldownType.INSULT)) removeOneSecond(string, CooldownType.INSULT);
			//			cooldowns.put(string, cooldowns.get(string) - 1000);
		}
	}

}
