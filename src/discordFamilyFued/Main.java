package discordFamilyFued;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import javax.security.auth.login.LoginException;
import javax.swing.JPanel;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

public class Main extends JPanel implements ActionListener {
	private static final long serialVersionUID = -7052336505253806151L;
	static String botName = "alethophobia";
	// channel to connect to allows us to dynamically assign channel
	static String channelString = "954205423257403452";
	// location of the user database which can be relocated anywhere
	static String userFileLocation = ".\\Userlist\\";
	static String questionFileLocation = ".\\questions\\";
	static questionDatabase questionDatabase = new questionDatabase();
	// global variable for the names in the user database to avoid redeclaring it
	// extensively
	static String[] pathnames;
	static File f = new File(userFileLocation);
	// administrator list to determine advanced commands
	static String[] admins = { "happihound" };
	static Random rand = new Random();
	static int roundNumber = 0;
	static boolean oneTimeRigged = false;
	static long botGameMessageID = -1;
	static boolean forceStart = false;
	static CountDownLatch latch = new CountDownLatch(1);
	static boolean inputOver = false;

	public static void main(String[] args) {
		writeLog("program started");
		// starts the drawing and connects the twitchbot
		new Thread(new Runnable() {
			@Override
			public void run() {

				connect1();
			}
		}).start();
		// continually redraws the display to keep it updated in parallel to other tasks

	}

	public static void startNewGame(ArrayList<String> Roster, MessageChannel channel) {
		writeLog("Starting new game");
		roundNumber = 0;
		channel.sendMessage("*Starting New Game!*").queue();
		if (Roster.size() < 2 && !forceStart) {
			channel.sendMessage("***Not enough players!***").queue();
			return;
		}

		for (int i = 0; Roster.size() > i;) {
			makeUserFile(Roster.get(i));
			i++;

		}

		newRound(Roster, channel);

	}

	public static void newRound(ArrayList<String> Roster, MessageChannel channel) {
		roundNumber++;
		alethophobia.ensureCapacity();
		int randomNumber = rand.nextInt(discordFamilyFued.questionDatabase.getLineCount("questions.txt"));
		String[] answers = questionDatabase.getAnswer(randomNumber);
		int[] answerValue = discordFamilyFued.questionDatabase.questionValue(randomNumber);
		long seed = System.nanoTime();
		Collections.shuffle(Arrays.asList(answers), new Random(seed));
		Collections.shuffle(Arrays.asList(answerValue), new Random(seed));
		channel.sendMessage("**Question Number " + randomNumber + "**").queue();

		sendMessageWithReactions(channel,
				questionDatabase.getQuestion(randomNumber) + "\n" + "**1. **" + answers[0] + "\n" + "**2. **"
						+ answers[1] + "\n" + "**3. **" + answers[2] + "\n" + "**4. **" + answers[3] + "\n" + "**5. **"
						+ answers[4] + "\n" + "**6. **" + answers[5],
				"\u0031\ufe0f\u20e3", "\u0032\ufe0f\u20e3", "\u0033\ufe0f\u20e3", "\u0034\ufe0f\u20e3",
				"\u0035\ufe0f\u20e3", "\u0036\ufe0f\u20e3");

		while (!inputOver) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		channel.sendMessage("**The answers are in! **").queue();
		int[] userAnswers = alethophobia.userChoices;
		for (int i = 0; Roster.size() > i;) {
			addUserPoints(Roster.get(i), answerValue[userAnswers[i]]);
			channel.sendMessage(Roster.get(i) + " chose" + " **" + (userAnswers[i] + 1) + ".** "
					+ answers[userAnswers[i]] + "" + "They earned " + answerValue[userAnswers[i]] + " points. Total: "
					+ getUserPoints(Roster.get(i)) + "!").queue();
			i++;
		}

	}

	public static void sendMessageWithReactions(MessageChannel channel, String embed, String... reactions) {
		channel.sendMessage(embed).queue(msg -> {
			for (String reaction : reactions) {
				msg.addReaction(reaction).queue();
			}
			botGameMessageID = msg.getIdLong();
		});

	}

	public static void makeUserFile(String user) {
		user = user.replaceAll(".txt", "");
		Main.writeLog("made new user " + user);
		try {
			FileOutputStream fos = new FileOutputStream(Main.userFileLocation + user + ".txt", false);
			String str = user + "\n" + "points=0";
			byte[] b = str.getBytes(); // converts string into bytes
			fos.write(b); // writes bytes into file
			fos.close(); // close the file
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int addUserPoints(String user, int points) {
		user = user.replaceAll(".txt", "");
		int basePoints = getUserPoints(user);
		if (!userExists(user)) {
			return -2;
		}

		user = user.replaceAll(".txt", "");

		List<String> newLines = new ArrayList<>();
		try {
			for (String line : Files.readAllLines(Paths.get(Main.userFileLocation + user + ".txt"),
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
			Files.write(Paths.get(Main.userFileLocation + user + ".txt"), newLines, StandardCharsets.UTF_8);
			return 1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	public static int getUserPoints(String user) {
		user = user.replaceAll("@", "");
		user = user.replaceAll(".txt", "");
		if (!userExists(user)) {
			return -2;
		}

		try {
			for (String line : Files.readAllLines(Paths.get(Main.userFileLocation + user + ".txt"),
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

	public static boolean userExists(String user) {
		pathnames = f.list();
		user = user.replaceAll(".txt", "");
		if (Arrays.asList(pathnames).contains(user.toLowerCase() + ".txt")) {
			return true;
		}
		return false;
	}

	public static void writeLog(String message) {
		try {
			FileWriter fw = new FileWriter(userFileLocation + "logFile.txt", true); // the true will append the new data
			fw.write(message + "\n");// appends the string to the file
			fw.close();
		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}
	}

	public static void connect1() {
		writeLog("bot connecting");
		try {
			JDA alethophobia = JDABuilder.createDefault("OTU0MjAyNzgwNjA2ODE2Mzc2.YjPslw.gvkvX-qNL_kzSWylMnDXDhKDH9s")
					.addEventListeners(new alethophobia()) // An instance of a class that will handle events.
					.build();
			alethophobia.awaitReady(); // Blocking guarantees that JDA will be completely loaded.
			System.out.println("Finished Building JDA!");
		} catch (LoginException e) {
			// If anything goes wrong in terms of authentication, this is the exception that
			// will represent it
			e.printStackTrace();
		} catch (InterruptedException e) {
			// Due to the fact that awaitReady is a blocking method, one which waits until
			// JDA is fully loaded,
			// the waiting can be interrupted. This is the exception that would fire in that
			// situation.
			// As a note: in this extremely simplified example this will never occur. In
			// fact, this will never occur unless
			// you use awaitReady in a thread that has the possibility of being interrupted
			// (Sync thread usage and interrupts)
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// error catching here

	}
}
