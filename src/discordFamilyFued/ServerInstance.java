package discordFamilyFued;

import java.io.File;
import java.util.Arrays;
import java.util.Random;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class ServerInstance {
	int[] userChoices;
	String[] pathnames;
	Random rand = new Random();
	File f = new File(Main.userFileLocation);
	boolean runCommands = true;
	long coolDownTime = 1;
	boolean acceptingInput = false;
	allowedGuild permittedGuild;
	Game newGame;

	public ServerInstance(allowedGuild guild) {
		permittedGuild = guild;

	}

	public String getName() {

		return permittedGuild.getName();
	}

	public String getID() {

		return permittedGuild.getServerID();
	}

	public void onMessageReceived(MessageReceivedEvent event) {
		pathnames = f.list();
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

						} else if (commandString.length == 2 && commandString[1].equalsIgnoreCase("force")
								&& commandString[0].equalsIgnoreCase("start") && userIsAdmin) {
							start(channel, true);

						} else if (commandString[0].equalsIgnoreCase("Newgame")) {
							if (!acceptingInput) {
								start(channel, false);
							}
						}

						if (runCommands && acceptingInput) {

							if (commandString[0].equalsIgnoreCase("join") && commandString.length == 1) {

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
		for (int i = 0; getGame().Roster.size() > i;) {
			if (userName.equalsIgnoreCase(getGame().Roster.get(i))) {
				int answerChoice = reactionToValue(msg) - 1;
				if (answerChoice < 0 || answerChoice > 6) {
					System.out.println("not a valid reaction!");
					return;
				}
				userChoices[i] = answerChoice;
				System.out.println("set message for user " + getGame().Roster.get(i) + " " + userChoices[i]);

			}

			i++;
		}

		for (int i = 0; userChoices.length > i;) {
			if (userChoices[i] == -1) {
				return;

			}

			i++;

		}
		Main.inputOver = true;

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
		if (!getGame().Roster.contains(user)) {
			getGame().Roster.add(user);
			channel.sendMessage(user + " joined the game!").queue();
			return;
		} else {
			channel.sendMessage(user + " you can't join twice!").queue();
		}

	}

	public Game getGame() {
		return newGame;
	}

	public void start(MessageChannel channel, boolean forceStart) {
		newGame = new Game();
		userChoices = new int[getGame().Roster.size()];
		for (int i = 0; userChoices.length > i;) {
			userChoices[i] = -1;
			i++;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (acceptingInput) {
					getGame().startNewGame(getGame().Roster, channel, forceStart, this);
					return;
				}
				channel.sendMessage("Starting new game..." + "\n" + "Do !join to join the game!").queue();
			}
		}).start();
		acceptingInput = true;

	}

	public void leave(String[] commandString, String user, MessageChannel channel) {
		if (getGame().Roster.contains(user)) {
			getGame().Roster.remove(new String(user));
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
		getGame().endGame(channel);
		channel.sendMessage("Operator " + user + " changed game status to offline").queue();
		Main.writeLog("Status was changed to off by " + user);
	}

	public void forcestop() {
		System.exit(0);
	}

	public void ensureCapacity() {

	}

}
