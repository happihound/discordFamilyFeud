package discordFamilyFued;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

class LogSystem {
  int runNumber;

  public LogSystem() {}

  public void makeLogFile() {
    File f = new File(Main.logFileLocation);
    runNumber = (f.list().length);
    try {
      FileOutputStream fos =
          new FileOutputStream(Main.logFileLocation + ("log_" + getRunNumber()) + ".log", false);
      String str = "\n" + "\n" + "New log for run: " + getRunNumber();
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

  public void warn(int warningNumber) {
    String message = "";
    switch (warningNumber) {
      case 1:
        message =
            "WARNING: Unable to change points for a user! This may have been caused by a read/write error";
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      default:
    }
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
