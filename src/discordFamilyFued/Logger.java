package discordFamilyFued;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
  int runNumber;

  public Logger() {}

  public void makeLogFile() {
    File f = new File(Main.logFileLocation);
    runNumber = (f.list().length);
    try {
      FileOutputStream fos =
          new FileOutputStream(Main.logFileLocation + ("log " + getRunNumber()) + ".log", false);
      String str = "New log for run: " + getRunNumber();
      byte[] b = str.getBytes(); // converts string into bytes
      fos.write(b); // writes bytes into file
      fos.close(); // close the file
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public int getRunNumber() {
    return runNumber;
  }

  public void Log(String message) {
    try {
      FileWriter fw =
          new FileWriter(
              Main.logFileLocation + ("log " + getRunNumber()) + ".log",
              true); // the true will append the new data
      fw.write("\n" + message); // appends the string to the file
      fw.close();
    } catch (IOException ioe) {
      System.err.println("IOException: " + ioe.getMessage());
    }
  }
}
