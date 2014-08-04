package com.github.zxskelobrine.networking.irc.bots.store.systems.karma;

public class KarmaUser {

	private String nick;
	private int karma;

	public KarmaUser(String nick, int karma) {
		this.nick = nick;
		this.karma = karma;
	}

	/**
	 * @return the nick
	 */
	public String getNick() {
		return nick;
	}

	/**
	 * @param nick
	 *            the nick to set
	 */
	public void setNick(String nick) {
		this.nick = nick;
	}

	/**
	 * @return the karma
	 */
	public int getKarma() {
		return karma;
	}

	public void incrementKarma() {
		karma++;
	}

	public void decrementKarma() {
		karma--;
	}

	public void setKarma(int karma) {
		this.karma = karma;
	}
}
