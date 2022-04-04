package discordFamilyFued;

import java.util.ArrayList;
import java.util.Arrays;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

class ServerInstance {
	int[] userChoices;
	int roundNumber;
	int gameProgress;
	ArrayList<String> Roster;
	final allowedGuild guild;
	final String fileLocation;
	Game newGame;

	public ServerInstance(allowedGuild guild) {
		gameProgress = -1;
		roundNumber = -1;
		this.guild = guild;
		Roster = new ArrayList<String>();
		fileLocation = Main.userFileLocation + getID() + "\\";

	}

	// Game progress is as follows
	// -2 = game is turned off by a moderator
	// -1 = no game has been started or is going on
	// 0 = game is in the joining phase, but not actively running
	// 1 = a round is currently in progress
	// 2 = users have finished inputing their choices
	// 3 = a round has ended and is ready to begin a new round

	public String getName() {

		return guild.getName();
	}

	public String getID() {

		return guild.getServerID();
	}

	public void makeNewGame(MessageChannel channel) {
		roundNumber = -1;
		channel.sendMessage("Making a new game...").queue();
		channel.sendMessage("Do !join to join the match!").queue();
		gameProgress = 0;
		Roster.clear();

	}

	public void makeNextRound(MessageChannel channel) {
		gameProgress = 3;
		if (roundNumber != 5) {
			start(channel);
		} else {
			newGame.endGame(channel);
		}

	}

	public ArrayList<String> getRoster() {
		return Roster;
	}

	public void onMessageReceived(MessageReceivedEvent event) {
		// pathnames = f.list();
		String userName = "happihound";
		User user = event.getAuthor();
		Message message = event.getMessage();
		MessageChannel channel = event.getChannel();
		String msg = message.getContentDisplay();

		if (event.isFromType(ChannelType.TEXT)) {
			if (!user.isBot()) {

				Guild Guild = event.getGuild();

				TextChannel textChannel = event.getTextChannel();
				channel = event.getTextChannel();

				userName = user.getName();
				userName = userName.toLowerCase();
				System.out.printf("(%s)[%s]<%s>: %s\n", guild.getName(), textChannel.getName(), userName, msg);

				if (event.getTextChannel() == Guild.getTextChannelById(guild.getChannelID())) {
					Character commandChar = '!';
					if (msg.length() == 0) {
						return;
					}
					if (msg.charAt(0) == commandChar) {
						String command = msg.substring(1);
						boolean userIsAdmin = false;
						if (Arrays.asList(Main.admins).contains(userName)) {
							userIsAdmin = true;
						}

						String[] commandString = command.split(" ");

						if (commandString[0].equalsIgnoreCase("on") && userIsAdmin) {
							on(userName, channel);
						}

						else if (commandString[0].equalsIgnoreCase("off") && userIsAdmin) {
							off(userName, channel);
						}

						else if (commandString[0].equalsIgnoreCase("forcestop") && userIsAdmin) {
							forcestop();

						} else if (commandString[0].equalsIgnoreCase("Newgame")
								|| (commandString[0].equalsIgnoreCase("new"))) {
							if (gameProgress == -1 || gameProgress == 3) {
								makeNewGame(channel);
							}
						}
						if (gameProgress == 0) {

							if (commandString[0].equalsIgnoreCase("join") && commandString.length == 1) {

								join(commandString, userName, channel);
							}

							else if (commandString[0].equalsIgnoreCase("leave") && commandString.length == 1) {

								leave(commandString, userName, channel);
							}

							else if (commandString[0].equalsIgnoreCase("start") && commandString.length == 1) {
								start(channel);
							}

						} else if (gameProgress == -2 && (command.contains("!join") || command.contains("!start")
								|| command.contains("!leave"))) {
							channel.sendMessage("The game is offline and commands are disabled").queue();
						} else if (gameProgress == -1 && (command.contains("!join") || command.contains("!start")
								|| command.contains("!leave"))) {
							channel.sendMessage("No game is running right now. Do !new to start a new game.").queue();
						}
					}

				}
			}

		}
	}

	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		User user = event.getUser();
		String userName = user.getName().toLowerCase();
		if (!(event.getMessageIdLong() == getGame().botGameMessageID)) {
			System.out.println("id missmatch, exiting");
			return;
		}

		if (userName.equalsIgnoreCase("alethophobia")) {
			return;
		}
		ReactionEmote message = event.getReactionEmote();

		String msg = message.getAsReactionCode(); // This returns a human readable version of the reaction. Similar to

		System.out.println(userName);
		for (int i = 0; Roster.size() > i;) {
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

		for (int i = 0; userChoices.length > i;) {
			if (userChoices[i] == -1) {
				return;

			}

			i++;

		}
		gameProgress = 2;

	}

	public int getGameProgress() {
		return gameProgress;
	}

	public void setGameProgress(int gameProgress) {
		this.gameProgress = gameProgress;
	}

	public int[] getUserChoices() {
		return userChoices;
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
		} else if (Roster.contains(user)) {
			channel.sendMessage(user + " you can't join twice!").queue();
			return;
		} else {
			Roster.add(user);
			channel.sendMessage(user + " joined the game!").queue();
		}

	}

	public Game getGame() {

		return newGame;
	}

	public void start(MessageChannel channel) {
		newGame = new Game(this);
		userChoices = new int[Roster.size()];
		for (int i = 0; userChoices.length > i;) {
			userChoices[i] = -1;
			i++;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (gameProgress == 3) {
					channel.sendMessage("**__Starting Round: " + (roundNumber + 2) + "__**").queue();
					getGame().startNewGame(channel, userChoices);
					return;
				} else if (gameProgress == 0) {
					gameProgress = 1;
					channel.sendMessage("**__Starting Round: 1__**").queue();
					getGame().startNewGame(channel, userChoices);
					return;
				}

			}
		}).start();

	}

	public void leave(String[] commandString, String user, MessageChannel channel) {
		if (Roster.contains(user)) {
			Roster.remove(new String(user));
			channel.sendMessage(user + " left the game!").queue();
			return;
		} else {
			channel.sendMessage(user + " you can't leave if you haven't joined!").queue();
		}

	}

	public void on(String user, MessageChannel channel) {
		channel.sendMessage("Operator " + user + " changed game status to online").queue();
		gameProgress = -1;
		Main.writeLog("Status was changed to on by " + user);
	}

	public void off(String user, MessageChannel channel) {
		if (gameProgress > 0) {
			getGame().endGame(channel);
		}
		channel.sendMessage("Operator " + user + " changed game status to offline").queue();
		Main.writeLog("Status was changed to off by " + user);
		gameProgress = -2;
	}

	public void forcestop() {
		System.exit(0);
	}

	public int getRoundNumber() {
		return roundNumber;
	}
}
