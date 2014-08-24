package com.github.zxskelobrine.networking.irc.bots.store.systems.internal.karma;

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

    /**
     * This will increase the karma of the user by one
     */
    public void incrementKarma() {
	karma++;
    }

    /**
     * This will reduce the karma of the user by one.
     */
    public void decrementKarma() {
	karma--;
    }

    /**
     * This will set the karma for the user.
     * 
     * @param karma
     *            - The amount of karma to set the user to.
     */
    public void setKarma(int karma) {
	this.karma = karma;
    }

    @Override
    public String toString() {
	return "Name: " + nick + "\nKarma: " + karma + "\n";
    }
}
