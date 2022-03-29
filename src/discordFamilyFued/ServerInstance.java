package discordFamilyFued;

import java.io.File;
import java.util.Arrays;
import java.util.Random;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ServerInstance {
	static int[] userChoices;
	static String[] pathnames;
	static Random rand = new Random();
	static File f = new File(Main.userFileLocation);
	static boolean runCommands = true;
	static long coolDownTime = 1;
	static boolean acceptingInput = false;
	static allowedGuild permittedGuild;

	public ServerInstance(allowedGuild guild) {
		permittedGuild = guild;

	}

	public String getName() {

		return permittedGuild.getName();
	}

	public String getID() {

		return permittedGuild.getServerID();
	}

	public static void messageForServer(MessageReceivedEvent event) {
		pathnames = f.list();
		String userName = "happihound";
		// Event specific information
		User user = event.getAuthor(); // The user that sent the message
		Message message = event.getMessage(); // The message that was received.
		MessageChannel channel = event.getChannel(); // This is the MessageChannel that the message was sent to.
														// This could be a TextChannel, PrivateChannel, or Group!

		String msg = message.getContentDisplay(); // This returns a human readable version of the Message. Similar to
		// what you would see in the client.

		if (event.isFromType(ChannelType.TEXT)) // If this message was sent to a Guild TextChannel
		{
			if (!user.isBot()) {

				Guild guild = event.getGuild(); // The Guild that this message was sent in. (note, in the API, Guilds
												// are
												// Servers)
				TextChannel textChannel = event.getTextChannel();
				channel = event.getTextChannel(); // The TextChannel that this message was sent to.
				// This Member that sent the message. Contains Guild specific
				// information
				// about the User!
				userName = user.getName();
				userName = userName.toLowerCase();
				System.out.printf("(%s)[%s]<%s>: %s\n", guild.getName(), textChannel.getName(), userName, msg);

				if (event.getTextChannel() == guild.getTextChannelById(permittedChannelId)) {
					allowedChannel = event.getTextChannel();
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

	public void join(String[] commandString, String user, MessageChannel channel) {
		if (!Game.Roster.contains(user)) {
			Game.Roster.add(user);
			channel.sendMessage(user + " joined the game!").queue();
			return;
		} else {
			channel.sendMessage(user + " you can't join twice!").queue();
		}

	}

	public void start(MessageChannel channel, boolean forceStart) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				if (acceptingInput) {
					Game.startNewGame(Game.Roster, channel, forceStart);
					return;
				}
				channel.sendMessage("Starting new game..." + "\n" + "Do !join to join the game!").queue();
			}
		}).start();
		acceptingInput = true;

	}

	public void leave(String[] commandString, String user, MessageChannel channel) {
		if (Game.Roster.contains(user)) {
			Game.Roster.remove(new String(user));
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
		Game.endGame(channel);
		channel.sendMessage("Operator " + user + " changed game status to offline").queue();
		Main.writeLog("Status was changed to off by " + user);
	}

	public void forcestop() {
		System.exit(0);
	}

	public static void ensureCapacity() {
		userChoices = new int[Game.Roster.size()];
		for (int i = 0; userChoices.length > i;) {
			userChoices[i] = -1;
			i++;
		}
	}

	public static void toggleRunning() {
		runCommands = !runCommands;
	}
}
