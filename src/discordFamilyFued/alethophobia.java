package discordFamilyFued;

import java.io.File;
import java.util.ArrayList;
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
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class alethophobia extends ListenerAdapter {
	static ArrayList<String> Roster = new ArrayList<String>();
	static int[] userChoices;
	static String[] pathnames;
	static Random rand = new Random();
	static File f = new File(Main.userFileLocation);
	static boolean runCommands = false;
	static long coolDownTime = 1;
	static TextChannel allowedChannel;
	static String permittedChannelId = "954205423257403452";
	static boolean acceptingInput = true;
	static int playerCount = 0;

	public alethophobia() {

	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
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

	@SubscribeEvent
	public void onReactionRemoved(MessageReactionRemoveEvent event) {
		System.out.println("HERE");

	}

	public void join(String[] commandString, String user, MessageChannel channel) {
		if (!Roster.contains(user)) {
			Roster.add(user);
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
				Game newGame = new Game();
				Game.startNewGame(Roster, channel, forceStart);
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
		runCommands = true;
		Main.writeLog("Status was changed to on by " + user);
	}

	public void off(String user, MessageChannel channel) {
		runCommands = false;
		channel.sendMessage("Operator " + user + " changed game status to offline").queue();
		Main.writeLog("Status was changed to off by " + user);
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		User user = event.getUser();
		String userName = user.getName().toLowerCase();
		if (!(event.getMessageIdLong() == Game.botGameMessageID)) {
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
		Main.inputOver = true;

	}

	public void forcestop() {
		System.exit(0);
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

	public static boolean returnStatus() {

		return runCommands;
	}

	public static void ensureCapacity() {
		userChoices = new int[Roster.size()];
		for (int i = 0; userChoices.length > i;) {
			userChoices[i] = -1;
			i++;
		}
	}

	public static void toggleRunning() {
		runCommands = !runCommands;
	}

}
