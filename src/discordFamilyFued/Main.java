package discordFamilyFued;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Main {

	static final String botName = "alethophobia";
	// channel to connect to allows us to dynamically assign channel
	// location of the user database which can be relocated anywhere
	static final String userFileLocation = ".\\Userlist\\";
	static final String questionFileLocation = ".\\questions\\";
	// global variable for the names in the user database to avoid redeclaring it
	// extensively
	// administrator list to determine advanced commands
	static final String[] admins = { "happihound" };
	// static Random rand = new Random();
	static final String[] permittedGuilds = { "849877469724803102", "917295004769210429" };
	static final String[] permittedChannels = { "954205423257403452", "958474528881778729" };
	static final String[] permittedServerNames = { "Sexy ass mom frickers", "just me" };

	// to do: add a restart method for servers
	public static void main(String[] args) {
		ArrayList<ServerInstance> servers = new ArrayList<ServerInstance>();
		for (int i = 0; permittedGuilds.length > i;) {
			allowedGuild guild = null;
			ServerInstance newServer = null;
			guild = new allowedGuild(permittedGuilds[i], permittedChannels[i], permittedServerNames[i]);
			newServer = new ServerInstance(guild);
			writeLog("made guild " + guild.getName() + " guildID:" + guild.getServerID() + " channelID:"
					+ guild.getChannelID());
			try {
				Files.createDirectories(Paths.get(userFileLocation + permittedGuilds[i] + "\\"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			servers.add(newServer);
			i++;
		}

		writeLog("program started");
		// starts the drawing and connects the twitchbot
		new Thread(new Runnable() {
			@Override
			public void run() {

				connect1(servers);
			}
		}).start();
		// continually redraws the display to keep it updated in parallel to other tasks

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

	public static void connect1(ArrayList<ServerInstance> servers) {
		writeLog("bot connecting");
		try {
			JDA alethophobia = JDABuilder.createDefault("OTU0MjAyNzgwNjA2ODE2Mzc2.YjPslw.gvkvX-qNL_kzSWylMnDXDhKDH9s")
					.addEventListeners(new alethophobia(servers)) // An instance of a class that will handle events.
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

}
