package com.github.zxskelobrine.networking.irc.bots.store.managers.internal;

import java.util.Timer;
import com.github.zxskelobrine.networking.irc.bots.store.systems.cooldown.CooldownTimerTask;

public class CoolDownManager {

	public static CooldownTimerTask cooldownTimerTask;

	public static void startCooldownManager() {
		if (cooldownTimerTask == null) cooldownTimerTask = new CooldownTimerTask();
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(cooldownTimerTask, 0, 1000);
	}

}
