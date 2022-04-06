package discordFamilyFued;

import java.io.FileWriter;
import java.io.IOException;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Main {

  static final String botName = "alethophobia";
  // root location of the user folder
  static final String userFileLocation = ".\\Userlist\\";
  // root location of where the question files are located
  static final String questionFileLocation = ".\\questions\\";
  // names of admins to determine operator commands
  static final String[] admins = {"happihound", "notwatty"};
  // information about the servers the bot is permitted to play in
  static final String[] permittedGuilds = {"849877469724803102", "917295004769210429"};
  static final String[] permittedChannels = {"954205423257403452", "958474528881778729"};
  static final String[] permittedServerNames = {"Sexy ass mom frickers", "just me"};

  public static void main(String[] args) {
    writeLog("program started");
    // start bot on new thread
    // this permits total bot restart if wanted
    new Thread(
            new Runnable() {
              @Override
              public void run() {
                connect1();
              }
            })
        .start();
  }

  public static void connect1() {
    writeLog("bot connecting");
    try {
      JDA alethophobia =
          JDABuilder.createDefault("OTU0MjAyNzgwNjA2ODE2Mzc2.YjPslw.gvkvX-qNL_kzSWylMnDXDhKDH9s")
              .addEventListeners(
                  new alethophobia()) // An instance of a class that will handle events.
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

  public static void writeLog(String message) {
    try {
      FileWriter fw =
          new FileWriter(
              userFileLocation + "logFile.txt", true); // the true will append the new data
      fw.write(message + "\n"); // appends the string to the file
      fw.close();
    } catch (IOException ioe) {
      System.err.println("IOException: " + ioe.getMessage());
    }
  }
}
