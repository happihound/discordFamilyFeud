package discordFamilyFued;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import net.dv8tion.jda.api.entities.MessageChannel;

class Game {
	boolean running;
	// String[] pathnames;
	questionDatabase questionDatabase;
	long botGameMessageID;
	ArrayList<String> Roster;
	ServerInstance server;

	public Game(ServerInstance thisServer) {

		this.server = thisServer;
		this.Roster = server.Roster;
		running = false;
		questionDatabase = new questionDatabase();
		botGameMessageID = -1;

		// TODO Auto-generated constructor stub
	}

	public void startNewGame(MessageChannel channel, int[] userAnswers1) {
		server.roundNumber++;
		final ArrayList<String> Roster = this.Roster;
		running = true;
		Main.writeLog("Starting new round");
		if (server.roundNumber == 0) {
			for (int i = 0; Roster.size() > i;) {
				makeUserFile(Roster.get(i));
				i++;

			}
		}

		if (server.roundNumber == 5) {
			endGame(channel);
			return;
		}
		newRound(Roster, channel, userAnswers1);
	}

	public void newRound(ArrayList<String> Roster, MessageChannel channel, int[] userAnswers1) {
		Random rand = new Random();
		int randomNumber = rand.nextInt(discordFamilyFued.questionDatabase.getLineCount("questions.txt"));
		String[] answers = questionDatabase.getAnswer(randomNumber);
		Integer[] answerValue = discordFamilyFued.questionDatabase.questionValue(randomNumber);
		long seed = System.nanoTime();
		Collections.shuffle(Arrays.asList(answers), new Random(seed));
		channel.sendMessage("**Question Number " + randomNumber + "**").queue();
		Collections.shuffle(Arrays.asList(answerValue), new Random(seed));
		Integer[] placementOfAnswer = new Integer[6];
		for (int i = 0; placementOfAnswer.length > i;) {
			placementOfAnswer[i] = i;
			i++;
		}
		Collections.shuffle(Arrays.asList(placementOfAnswer), new Random(seed));
		sendMessageWithReactions(channel,
				questionDatabase.getQuestion(randomNumber) + "\n" + "**1. **" + answers[0] + "\n" + "**2. **"
						+ answers[1] + "\n" + "**3. **" + answers[2] + "\n" + "**4. **" + answers[3] + "\n" + "**5. **"
						+ answers[4] + "\n" + "**6. **" + answers[5],
				"\u0031\ufe0f\u20e3", "\u0032\ufe0f\u20e3", "\u0033\ufe0f\u20e3", "\u0034\ufe0f\u20e3",
				"\u0035\ufe0f\u20e3", "\u0036\ufe0f\u20e3");

		while (server.getGameProgress() != 2) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		channel.sendMessage("**The answers are in! **").queue();
		int[] userAnswers = userAnswers1;
		String[] answers2 = questionDatabase.getAnswerAndValue(randomNumber);
		channel.sendMessage("\n" + "**1. **" + answers2[0] + "\n" + "**2. **" + answers2[1] + "\n" + "**3. **"
				+ answers2[2] + "\n" + "**4. **" + answers2[3] + "\n" + "**5. **" + answers2[4] + "\n" + "**6. **"
				+ answers2[5] + "\n").queue();
		for (int i = 0; Roster.size() > i;) {
			addUserPoints(Roster.get(i), answerValue[userAnswers[i]]);
			channel.sendMessage(Roster.get(i) + " chose" + " **" + (placementOfAnswer[userAnswers[i]] + 1) + ".** "
					+ "\"" + answers[userAnswers[i]] + "\"" + " They earned " + answerValue[userAnswers[i]]
					+ " points. Total: " + getUserPoints(Roster.get(i)) + "!").queue();
			i++;
		}

		server.setGameProgress(3);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (server.getRoundNumber() >= 2) {
			endGame(channel);
		} else {
			server.makeNextRound(channel);
		}

		return;

	}

	@SuppressWarnings("rawtypes")
	public void endGame(MessageChannel channel) {
		String endScore = "";
		SortedMap<Integer, String> tm1 = new TreeMap<Integer, String>();

		for (int i = 0; Roster.size() > i;) {
			tm1.put(getUserPoints(Roster.get(i)), Roster.get(i));

			i++;
		}
		for (Map.Entry mapElement : tm1.entrySet()) {
			int key = (int) mapElement.getKey();
			String value = (String) mapElement.getValue();
			endScore = endScore + value + " earned " + key + " points! " + "\n";

		}
		channel.sendMessage("**Thanks for playing!" + "\n" + endScore + "You can start a new game with !new.**")
				.queue();
		server.setGameProgress(-1);
		running = false;
		Thread thread = Thread.currentThread();
		thread.stop();
	}

	public void endGame() {
		server.setGameProgress(-1);
		running = false;
		Thread thread = Thread.currentThread();
		thread.stop();
	}

	public void sendMessageWithReactions(MessageChannel channel, String embed, String... reactions) {
		channel.sendMessage(embed).queue(msg -> {
			for (String reaction : reactions) {
				msg.addReaction(reaction).queue();
			}
			botGameMessageID = msg.getIdLong();
		});

	}

	public void makeUserFile(String user) {
		user = user.replaceAll(".txt", "");
		Main.writeLog("made new user " + user + " in server " + server.getName());
		try {
			FileOutputStream fos = new FileOutputStream(server.fileLocation + user + ".txt", false);
			String str = user + "\n" + "points=0";
			byte[] b = str.getBytes(); // converts string into bytes
			fos.write(b); // writes bytes into file
			fos.close(); // close the file
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int addUserPoints(String user, int points) {
		user = user.replaceAll(".txt", "");
		int basePoints = getUserPoints(user);
		if (!userExists(user)) {
			return -2;
		}

		user = user.replaceAll(".txt", "");

		List<String> newLines = new ArrayList<>();
		try {
			for (String line : Files.readAllLines(Paths.get(server.fileLocation + user + ".txt"),
					StandardCharsets.UTF_8)) {
				if (line.contains("points=")) {
					newLines.add("points=" + (basePoints + points));

				}

				else {
					newLines.add(line);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		try {
			Files.write(Paths.get(server.fileLocation + user + ".txt"), newLines, StandardCharsets.UTF_8);
			return 1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	public int getUserPoints(String user) {
		user = user.replaceAll("@", "");
		user = user.replaceAll(".txt", "");
		if (!userExists(user)) {
			return -2;
		}

		try {
			for (String line : Files.readAllLines(Paths.get(server.fileLocation + user + ".txt"),
					StandardCharsets.UTF_8)) {
				if (line.contains("points=")) {
					if (!(Integer.parseInt(line.replaceAll("[^0-9]*", "")) >= 0)) {
						return -1;
					}

					return Integer.parseInt(line.replaceAll("[^0-9]*", ""));

				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block

			e1.printStackTrace();
		}
		return -1;

	}

	public boolean userExists(String user) {
		File f = new File(server.fileLocation);
		String[] pathnames = f.list();
		user = user.replaceAll(".txt", "");
		if (Arrays.asList(pathnames).contains(user.toLowerCase() + ".txt")) {
			return true;
		}
		return false;
	}

	public boolean getRunning() {
		return running;
	}

}
