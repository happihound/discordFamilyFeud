package discordFamilyFued;

import java.util.ArrayList;
import java.util.Arrays;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

class ServerInstance {
  int[] userChoices;
  int roundNumber;
  int gameState;
  ArrayList<String> Roster;
  final allowedGuild guild;
  final String fileLocation;
  alethophobia alethophobia;
  Game newGame;
  LogSystem logger;

  public ServerInstance(allowedGuild guild, alethophobia alethophobia) {
    this.logger = new LogSystem();

    this.alethophobia = alethophobia;
    setGameState(-1);
    setRoundNumber(-1);
    this.guild = guild;
    Roster = new ArrayList<String>();
    fileLocation = Main.userFileLocation + getID() + "\\";
  }

  public boolean validMessage(MessageReceivedEvent event) {
    boolean valid = false;
    Guild Guild = event.getGuild();
    User user = event.getAuthor();
    String msg = event.getMessage().getContentDisplay();
    Character commandChar = '!';
    if (user.isBot()) {
      return false;
    } else if (msg.length() == 0) {
      return false;
    } else if (event.isFromType(ChannelType.TEXT)
        && (event.getTextChannel() == Guild.getTextChannelById(guild.getChannelID()))
        && msg.charAt(0) == commandChar) {
      return true;
    }

    return valid;
  }

  public boolean isAdmin(String userName) {
    if (Arrays.asList(Main.admins).contains(userName)) {
      return true;
    }
    return false;
  }

  public void onMessageReceived(MessageReceivedEvent event) {
    String msg = event.getMessage().getContentDisplay();
    TextChannel textChannel = event.getTextChannel();
    String userName = event.getAuthor().getName().toLowerCase();
    System.out.printf("(%s)[%s]<%s>: %s\n", guild.getName(), textChannel.getName(), userName, msg);
    if (!validMessage(event)) {
      return;
    }
    String[] commandString = msg.substring(1).split(" ");
    for (int i = 0; commandString.length > i; ) {
      commandString[i].toLowerCase();
      i++;
    }
    logger.Log("Received new command in " + this.getName() + " from user " + userName);

    if (isAdmin(userName)) {
      switch (commandString[0]) {
        case "on":
          on(userName, textChannel);
          return;
        case "off":
          off(userName, textChannel);
          return;
        case "stop":
          stop(textChannel);
          return;
        default:
      }
    }
    if (commandString[0].equalsIgnoreCase("end")) {
      endGameServer(isAdmin(userName), userName, textChannel);
      return;
    }
    if (commandString[0].equalsIgnoreCase("Newgame")
        || (commandString[0].equalsIgnoreCase("new"))) {
      if (getGameState() == -1 || getGameState() == 3) {
        makeNewGame(textChannel);
        return;
      }
    } else if (commandString[0].equalsIgnoreCase("restart")) {
      restart(textChannel);
      return;
    }
    if (getGameState() == 0 && commandString.length == 1) {

      switch (commandString[0]) {
        case "join":
          join(commandString, userName, textChannel);
          return;
        case "leave":
          leave(commandString, userName, textChannel);
          return;
        case "start":
          start(textChannel);
          return;

        default:
      }
    }
    if (getGameState() == -2
        && (msg.contains("!join")
            || msg.contains("!start")
            || msg.contains("!leave")
            || msg.contains("new")
            || msg.contains("newgame"))) {
      textChannel.sendMessage("The game is offline and commands are disabled").queue();
    }
    if (getGameState() == -1
        && (msg.contains("!join")
            || msg.contains("!start")
            || msg.contains("!leave")
            || msg.contains("new")
            || msg.contains("newgame"))) {
      textChannel.sendMessage("No game is running right now. Do !new to start a new game.").queue();
    }
  }

  public void makeNewGame(MessageChannel channel) {
    setRoundNumber(-1);
    channel.sendMessage("Making a new game...").queue();
    channel.sendMessage("Do !join to join the match!").queue();
    setGameState(0);
    Roster.clear();
  }

  public void makeNextRound(MessageChannel channel) {
    setGameState(3);
    if (getRoundNumber() != 5) {
      start(channel);
    } else {
      newGame.endGame(channel);
    }
  }

  public void onMessageReactionAdd(MessageReactionAddEvent event) {
    String userName = event.getUser().getName().toLowerCase();
    if (getGameState() <= 0 || !(event.getMessageIdLong() == getGame().getbotGameMessageID())) {
      System.out.println("No game running, or id mismatch, exiting");
      return;
    }

    if (userName.equalsIgnoreCase("alethophobia")) {
      return;
    }

    String msg = event.getReactionEmote().getAsReactionCode();

    System.out.println(userName);
    for (int i = 0; Roster.size() > i; ) {
      if (userName.equalsIgnoreCase(Roster.get(i))) {
        int answerChoice = reactionToValue(msg) - 1;
        if (answerChoice < 0 || answerChoice > 6) {
          System.out.println("not a valid reaction!");
          return;
        }
        userChoices[i] = answerChoice;
        System.out.println("set message for user " + Roster.get(i) + " " + userChoices[i]);
      }

      i++;
    }

    for (int i = 0; userChoices.length > i; ) {
      if (userChoices[i] == -1) {
        return;
      }

      i++;
    }
    setGameState(2);
  }

  public void endGameServer(boolean isAdmin, String user, TextChannel channel) {
    if (getGameState() >= 0 && (Roster.size() == 1) || isAdmin == true) {
      Roster.clear();
      if (isAdmin && getGameState() > 0) {
        channel.sendMessage("Admin " + user + " ended the game!").queue();
        logger.Log("Admin" + user + " ended the game in server: " + this.getName());
        getGame().endGame(channel);
        return;
      } else if (getGameState() > 0) {
        channel.sendMessage(user + " ended their game").queue();
        logger.Log(user + " ended their single player game in server: " + this.getName());
        getGame().endGame(channel);
        return;
      }
      channel.sendMessage(user + " ended their game").queue();
    } else {
      logger.Log(user + " tried to end a game with multiple people in server: " + this.getName());
      channel.sendMessage(user + " you can't end a game with multiple people!").queue();
    }
  }

  public int reactionToValue(String msg) {

    int value = -1;

    if (msg.equals("\u0031\ufe0f\u20e3")) {
      value = 1;

    } else if (msg.equals("\u0032\ufe0f\u20e3")) {
      value = 2;
    } else if (msg.equals("\u0033\ufe0f\u20e3")) {
      value = 3;
    } else if (msg.equals("\u0034\ufe0f\u20e3")) {
      value = 4;
    } else if (msg.equals("\u0035\ufe0f\u20e3")) {
      value = 5;
    } else if (msg.equals("\u0036\ufe0f\u20e3")) {
      value = 6;
    }
    return value;
  }

  public void join(String[] commandString, String user, MessageChannel channel) {
    if (Roster == null || Roster.size() == 0) {
      Roster.add(user);
      channel.sendMessage(user + " joined the game!").queue();
      logger.Log(user + " joined an empty game in server: " + this.getName());
    } else if (Roster.contains(user)) {
      channel.sendMessage(user + " you can't join twice!").queue();
      logger.Log(
          user + " tried to join but was already in the roster in server: " + this.getName());
      return;
    } else {
      Roster.add(user);
      channel.sendMessage(user + " joined the game!").queue();
      logger.Log(user + " joined the game in server: " + this.getName());
    }
  }

  public void start(MessageChannel channel) {
    logger.Log("Attempting to create new game in server: " + getName());
    newGame = new Game(this);
    userChoices = new int[Roster.size()];
    for (int i = 0; userChoices.length > i; ) {
      userChoices[i] = -1;
      i++;
    }
    new Thread(
            new Runnable() {
              @Override
              public void run() {
                if (getGameState() == 3) {
                  logger.Log(
                      "Started round " + (getRoundNumber() + 2) + " in server: " + getName());
                  channel
                      .sendMessage("**__Starting Round: " + (getRoundNumber() + 2) + "__**")
                      .queue();
                  getGame().startNewGame(channel, userChoices);
                  return;
                } else if (getGameState() == 0) {
                  logger.Log("Started a new game in server: " + getName());
                  setGameState(1);
                  channel.sendMessage("**__Starting Round: 1__**").queue();
                  getGame().startNewGame(channel, userChoices);
                  return;
                }
              }
            })
        .start();
  }

  public void leave(String[] commandString, String user, MessageChannel channel) {
    if (Roster.contains(user)) {
      Roster.remove(new String(user));
      channel.sendMessage(user + " left the game!").queue();
      logger.Log("Removed user: " + user + " from the roster in server " + this.getName());
      return;
    } else {
      channel.sendMessage(user + " you can't leave if you haven't joined!").queue();
      logger.Log("User " + user + " tried to leave but wasn't in the roster");
      return;
    }
  }

  public void on(String user, MessageChannel channel) {
    channel.sendMessage("Operator " + user + " enabled the game").queue();
    setGameState(-1);
    logger.Log("Status was changed to on by " + user);
  }

  public void off(String user, MessageChannel channel) {
    if (getGameState() > 0) {
      getGame().endGame();
    }
    channel.sendMessage("Operator " + user + " disabled the game").queue();
    logger.Log("Status was changed to off by " + user);
    setGameState(-2);
  }

  public void stop(MessageChannel channel) {
    alethophobia.stopServer(this, guild, channel);
  }

  public void restart(MessageChannel channel) {
    alethophobia.restartServer(this, channel);
  }

  public void setRoundNumber(int updatedRoundNumber) {
    roundNumber = updatedRoundNumber;
  }

  public void setGameState(int updatedGameState) {
    logger.Log("Game state was updated to \"" + getGameState(updatedGameState) + "\"");
    gameState = updatedGameState;
  }

  public int[] getUserChoices() {
    return userChoices;
  }

  public int getGameState() {
    return gameState;
  }

  public String getGameState(int state) {
    switch (state) {
      case -2:
        {
          return "Game was disabled by a moderator";
        }
      case -1:
        {
          return "Game was disabled by a moderator";
        }
      case 0:
        {
          return "game is in the joining phase, no round actively running";
        }
      case 1:
        {
          return "a round is currently in progress";
        }
      case 2:
        {
          return "users have finished inputing their choices";
        }
      case 3:
        {
          return "a round has ended and is ready to begin a new round";
        }
      default:
        {
          return "ERROR NO VALID STATE";
        }
    }
  }

  public int getRoundNumber() {
    return roundNumber;
  }

  public String getFileLocation() {
    return fileLocation;
  }

  public String getName() {

    return guild.getName();
  }

  public long getID() {

    return guild.getServerID();
  }

  public long getChannelID() {
    return guild.getChannelID();
  }

  public Game getGame() {

    return newGame;
  }
}
