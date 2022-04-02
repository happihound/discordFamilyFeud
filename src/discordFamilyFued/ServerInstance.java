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
	boolean runCommands;
	boolean gameRunning;
	boolean acceptingInput;
	boolean inputOver;
	ArrayList<String> Roster;
	allowedGuild permittedGuild;
	String fileLocation;
	Game newGame;

	public ServerInstance(allowedGuild guild) {
		roundNumber = -1;
		permittedGuild = guild;
		gameRunning = false;
		Roster = new ArrayList<String>();
		inputOver = false;
		acceptingInput = false;
		runCommands = true;
		fileLocation = Main.userFileLocation + getID() + "\\";

	}

	public String getName() {

		return permittedGuild.getName();
	}

	public String getID() {

		return permittedGuild.getServerID();
	}

	public void makeNewGame(MessageChannel channel) {
		roundNumber = -1;
		channel.sendMessage("Making a new game...").queue();
		channel.sendMessage("Do !join to join the match!").queue();
		acceptingInput = true;
		Roster.clear();

	}

	public void makeNextRound(MessageChannel channel) {
		roundNumber++;
		if (roundNumber != 5) {
			channel.sendMessage("Making a new round...").queue();
			acceptingInput = true;
			start(channel, true);
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

				Guild guild = event.getGuild();

				TextChannel textChannel = event.getTextChannel();
				channel = event.getTextChannel();

				userName = user.getName();
				userName = userName.toLowerCase();
				System.out.printf("(%s)[%s]<%s>: %s\n", guild.getName(), textChannel.getName(), userName, msg);

				if (event.getTextChannel() == guild.getTextChannelById(permittedGuild.getChannelID())) {
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
							if (!acceptingInput) {
								makeNewGame(channel);
							}
						} else if ((commandString[0].equalsIgnoreCase("next") && (Roster.size() != 0))) {
							makeNextRound(channel);
						}

						if (runCommands && acceptingInput) {

							if (commandString.length == 2 && commandString[1].equalsIgnoreCase("force")
									&& commandString[0].equalsIgnoreCase("start") && userIsAdmin) {
								start(channel, true);

							}

							else if (commandString[0].equalsIgnoreCase("join") && commandString.length == 1) {

								join(commandString, userName, channel);
							}

							else if (commandString[0].equalsIgnoreCase("leave") && commandString.length == 1) {

								leave(commandString, userName, channel);
							}

							else if (commandString[0].equalsIgnoreCase("start") && commandString.length == 1) {
								start(channel, false);
							}

							else if (runCommands == false) {
								if (command.contains("!join") || command.contains("!start")
										|| command.contains("!leave")) {
									channel.sendMessage("The game is offline and commands are disabled").queue();
								}
							} else if (acceptingInput == false) {
								if (command.contains("!join") || command.contains("!start")
										|| command.contains("!leave")) {
									channel.sendMessage("Not currently accepting user input.").queue();
								}
							}
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
		inputOver = true;

	}

	public boolean getInputStatus() {
		return inputOver;
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

	public boolean gameStatus() {

		return gameRunning;
	}

	public void gameStatus(boolean setting) {
		gameRunning = setting;
	}

	public void start(MessageChannel channel, boolean forceStart) {
		gameRunning = true;
		newGame = new Game(this);
		userChoices = new int[Roster.size()];
		for (int i = 0; userChoices.length > i;) {
			userChoices[i] = -1;
			i++;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (acceptingInput) {
					getGame().startNewGame(channel, forceStart, userChoices);
					return;
				}
				channel.sendMessage("Starting new game..." + "\n" + "Do !join to join the game!").queue();
			}
		}).start();
		acceptingInput = true;

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
		runCommands = true;
		Main.writeLog("Status was changed to on by " + user);
	}

	public void off(String user, MessageChannel channel) {
		runCommands = false;
		if (gameStatus()) {
			getGame().endGame(channel);
		}
		channel.sendMessage("Operator " + user + " changed game status to offline").queue();
		Main.writeLog("Status was changed to off by " + user);
	}

	public void forcestop() {
		System.exit(0);
	}

	public void ensureCapacity() {

	}

	public void inputStatus(boolean b) {
		// TODO Auto-generated method stub
		inputOver = b;
	}

}
