package discordFamilyFued;

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
  // location of the log folder
  static final String logFileLocation = ".\\logFiles\\";
  // information about the servers the bot is permitted to play in
  static final long[] permittedGuilds = {849877469724803102L, 917295004769210429L};
  static final long[] permittedChannels = {954205423257403452L, 958474528881778729L};
  static final String[] permittedServerNames = {"sexy ass mom frickers", "just me"};
  static Logger Logger = new Logger();

  public static void main(String[] args) {
    Logger.makeLogFile();
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
    Logger.Log("bot connecting");
    try {
      JDA alethophobia =
          JDABuilder.createDefault("OTU0MjAyNzgwNjA2ODE2Mzc2.YjPslw.gvkvX-qNL_kzSWylMnDXDhKDH9s")
              .addEventListeners(
                  new alethophobia()) // An instance of a class that will handle events.
              .build();
      alethophobia.awaitReady();
      Logger.Log(
          "Finished Building JDA"); // Blocking guarantees that JDA will be completely loaded.
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

  public static void writeLog(String logMessage) {
    Logger.Log(logMessage);
  }
}
