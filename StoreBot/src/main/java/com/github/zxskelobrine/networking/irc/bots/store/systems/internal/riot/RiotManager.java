package com.github.zxskelobrine.networking.irc.bots.store.systems.internal.riot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class RiotManager {

	private List<RiotUser> rioters;
	private final int TOTAL_RIOT_TIME = 180000;
	private Timer timer;
	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			synchronized (rioters) {
				for (RiotUser riotUser : rioters) {
					riotUser.secondDown();
					if (!riotUser.hasTimeLeft()) rioters.remove(riotUser);
				}
			}
		}
	};

	public void startRiotControl() {
		rioters = new ArrayList<RiotUser>();
		timer = new Timer();
		timer.scheduleAtFixedRate(task, 0, 1000);
	}

	public synchronized void addRiotUser(final String nick) {
		if (rioters == null) rioters = new ArrayList<RiotUser>();
		new Thread() {
			@Override
			public void run() {
				synchronized (rioters) {
					RiotUser riotUser = new RiotUser(nick, TOTAL_RIOT_TIME);
					CopyOnWriteArrayList<RiotUser> rus = new CopyOnWriteArrayList<>(rioters);
					boolean stc = true;
					for (RiotUser riotUser2 : rus) {
						if (riotUser.getNick().equals(riotUser2.getNick())) {
							stc = false;
							break;
						}
					}
					if (stc) rioters.add(new RiotUser(nick, TOTAL_RIOT_TIME));
				}
			}
		}.start();
	}

	public synchronized int getRioters() {
		synchronized (rioters) {
			return rioters.size();
		}
	}

}
