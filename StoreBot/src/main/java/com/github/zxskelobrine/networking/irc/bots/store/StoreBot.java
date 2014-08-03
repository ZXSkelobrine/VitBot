package com.github.zxskelobrine.networking.irc.bots.store;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.cap.TLSCapHandler;
import org.pircbotx.dcc.ReceiveChat;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.IncomingChatRequestEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class StoreBot extends ListenerAdapter<PircBotX> {

  private FileOutputStream fos;
  private FileOutputStream suggestionStream;

  private File output;

  private PircBotX bot;

  private static String hostname;
  private static String channel;

  private String[] unchartedLyrics = new String[] {"Du", "Du", "Du", "Doo", "Du", "Dududoo", "du",
      "du", "du", "doo", "dududoo", "Du", "Du", "Dooooo", "do do do", "du do do", "doooooo",
      "duuuu dooooo", "dooooooooOOOOOOOOooooooo", "DuuuuuuuUUUUUUuuuuuuuuuu",
      "DoooooooouuuuuuuoooOOOUU", "DO DO DO DO DO DO DO do do", "du du du du du du du du", "DO DO",
      "(rumpa bum bum)", "bum bum bum"};
  private String[] riotStrings = new String[] {"%NICK% waves their arms like a retarted cow",
      "%NICK% slaps %RANDNICK% with a %SIZE% trout"};

  private RiotControl control;

  private static void log(String message) {
    boolean isThePopeACatholic = true;
    if (!isThePopeACatholic) {
      System.out.println(message);
    }
  }


  @SuppressWarnings({"unchecked", "rawtypes"})
  public static void main(String[] args) throws IOException {
    if (args.length >= 2) {
      hostname = args[0];
      log("Set hostnae");
      channel = args[1];
      log("Set channel");
      StoreBot sb = new StoreBot();
      log("Storebot created");
      log("Creating config");
      Configuration<PircBotX> configuration;
      try {
        configuration =
            new Configuration.Builder()
                .setName("VitBot")
                .setLogin("LQ")
                .setAutoNickChange(false)
                .setCapEnabled(true)
                .addCapHandler(
                    new TLSCapHandler(new UtilSSLSocketFactory().trustAllCertificates(), true))
                .addListener(sb).setServerHostname(hostname)// irc.quakenet.org
                .addAutoJoinChannel(channel).buildConfiguration();//
        log("Config created");
        PircBotX botX = new PircBotX(configuration);
        log("Bot created");
        sb.bot = botX;
        log("Bot set");
        botX.startBot();
        log("Bot started");
      } catch (IOException e) {
        e.printStackTrace();
      } catch (IrcException e) {
        e.printStackTrace();
      }
    } else {
      System.err.println("Invalid arguments.");
    }
  }

  public StoreBot() throws IOException {
    control = new RiotControl();
    control.startRiotControl();
    File suggestionFile =
        new File(
            "E:\\IRC Logs\\Bot Store\\Quakenet\\Project Awesome\\VitBot\\Suggestions\\Suggestions - "
                + new SimpleDateFormat("dd-MM-yy - hh-mm").format(Calendar.getInstance().getTime())
                + ".txt");
    if (!suggestionFile.exists())
      suggestionFile.createNewFile();
    output =
        new File("E:\\IRC Logs\\Bot Store\\Quakenet\\Project Awesome\\VitBot\\"
            + new SimpleDateFormat("dd-MM-yy - hh-mm").format(Calendar.getInstance().getTime())
            + ".txt");
    suggestionStream = new FileOutputStream(suggestionFile, true);
    log("Output file set");
    if (!output.exists())
      output.createNewFile();
    log("Output file created if needed");
    fos = new FileOutputStream(output, true);
    log("Output stream set");
  }

  @Override
  public void onJoin(JoinEvent<PircBotX> event) throws Exception {

    log("Channel joined");
    String message = "[" + event.getTimestamp() + "] JOIN: " + event.getChannel() + "\n";
    log("Output made");
    fos.write(message.getBytes());
    log("Output outed");
    super.onJoin(event);
  }

  @Override
  public void onGenericMessage(GenericMessageEvent<PircBotX> event) throws Exception {
    log("Generic message");
    String message =
        "[" + event.getTimestamp() + "]" + event.getUser().getNick() + ": " + event.getMessage()
            + "\n";
    fos.write(message.getBytes());
    log("Written");
    String responce = respondToMessage(event.getMessage(), event.getUser().getNick(), event);
    if (responce != null) {
      bot.sendIRC().message(channel, responce);
      log("Message sent");
    }
    super.onGenericMessage(event);
  }

  @Override
  public void onIncomingChatRequest(IncomingChatRequestEvent<PircBotX> event) throws Exception {
    ReceiveChat rc = event.accept();
    rc.sendLine("Go away");
    rc.close();
    super.onIncomingChatRequest(event);
  }

  Random random = new Random();

  public String respondToMessage(String message, String nick, GenericMessageEvent<PircBotX> event) {
    log("Message: " + message);
    log("Nick: " + nick);
    if (message.toLowerCase().contains("norway")) {
      return "http://www.oljefondet.no/ NORWAY!";
    }
    if (message.toLowerCase().startsWith("idiot")) {
      String[] spaceSplit = message.split("\\s+");
      String username = spaceSplit[1];
      String reason = "";
      if (spaceSplit.length > 2) {
        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 2; i < spaceSplit.length; i++) {
          reasonBuilder.append(spaceSplit[i] + " ");
        }
        reason = " because " + reasonBuilder.toString();
      } else if (spaceSplit.length < 2) {
        reason = "";
      }

      return username + " you are an idiot" + reason;
    }
    if (message.toLowerCase().startsWith("vitbot")) {
      String[] spaceSplit = message.split("\\s+");
      if (spaceSplit[1].equalsIgnoreCase("suggestion")) {
        try {
          suggestionStream.write((nick + ": " + message + "\n").getBytes());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    if (message.toLowerCase().startsWith("!lyric")) {
      return nick + ": " + unchartedLyrics[random.nextInt(unchartedLyrics.length)];
    }
    if (message.toLowerCase().contains("!riot")) {
      control.addRiotUser(nick);
      int rioters = control.getRioters();
      if (rioters == 1) {
        bot.sendIRC().message(channel, rioters + " person is currently rioting");
      } else {
        bot.sendIRC().message(channel, rioters + " people are currently rioting");
      }
      String riotMessage = riotStrings[random.nextInt(riotStrings.length)];
      riotMessage = riotMessage.replace("%NICK%", nick);
      if (riotMessage.contains("%RANDNICK%")) {
        for (Channel channel : event.getUser().getChannels()) {
          if (channel.getName().contains(this.channel)) {
            riotMessage =
                riotMessage.replace(
                    "%RANDNICK%",
                    channel.getUsers().asList()
                        .get(random.nextInt(channel.getUsers().asList().size())).getNick());
          }
        }
      }
      if (riotMessage.contains("%SIZE%")) {
        switch (random.nextInt(3)) {
          case 0:
            riotMessage = riotMessage.replace("%SIZE%", "small");
            break;
          case 1:
            riotMessage = riotMessage.replace("%SIZE%", "medium");
            break;
          case 2:
            riotMessage = riotMessage.replace("%SIZE%", "large");
            break;
        }
      }
      return riotMessage;
    }
    return null;
  }
}
