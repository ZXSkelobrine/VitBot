package com.github.zxskelobrine.networking.irc.bots.store.systems.internal.slap;

public class SlapUser {

	private int currentHealth;
	private int maxHealth;
	private String nick;

	public SlapUser(int maxHealth, String nick) {
		this.currentHealth = maxHealth;
		this.maxHealth = maxHealth;
		this.nick = nick;
	}

	public void heal(int amount) {
		if ((currentHealth + amount) > maxHealth) {
			currentHealth = maxHealth;
		} else {
			currentHealth += amount;
		}
	}

	public boolean damage(int amount) {
		if ((currentHealth - amount) < 0) {
			currentHealth = maxHealth;
			return true;
		} else {
			currentHealth -= amount;
			return false;
		}
	}

	public String getHealth() {
		return currentHealth + "/" + maxHealth;
	}

	public String getNick() {
		return nick;
	}

}
