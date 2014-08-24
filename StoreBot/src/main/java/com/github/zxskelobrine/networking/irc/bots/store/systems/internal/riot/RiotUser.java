package com.github.zxskelobrine.networking.irc.bots.store.systems.internal.riot;

public class RiotUser {

  private String nick;
  private long millisStartTime;
  private long totalTime;
  private long timeLeft;

  public RiotUser(String nick, int totalTime) {
    this.nick = nick;
    this.totalTime = totalTime;
    this.millisStartTime = System.currentTimeMillis();
    this.timeLeft = millisStartTime + this.totalTime;
  }

  public String getNick() {
    return nick;
  }

  public void secondDown() {
    timeLeft -= 1000;
  }

  public boolean hasTimeLeft() {
    return !(timeLeft <= 0);
  }

}
