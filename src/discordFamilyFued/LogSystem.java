package discordFamilyFued;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

class LogSystem {
  int runNumber;

  public LogSystem(int runNumber) {
    this.runNumber = runNumber;
  }

  public void makeLogFile() {
    try {
      FileOutputStream fos =
          new FileOutputStream(Main.logFileLocation + ("log_" + getRunNumber()) + ".log", false);
      String str = "New log for run: " + getRunNumber();
      byte[] b = str.getBytes(); // converts string into bytes
      fos.write(b); // writes bytes into file
      fos.close(); // close the file
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public int getRunNumber() {
    return Main.getRunNumber();
  }

  public void Log(String message) {
    try {
      FileWriter fw =
          new FileWriter(
              Main.logFileLocation + ("log_" + getRunNumber()) + ".log",
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
        message = "WARNING: Unable to read or write a file!";
      case 2:
        message =
            "WARNING: Unable to change points for a user! This may have been caused referencing a user that doesn't exist";
      case 3:
        message =
            "WARNING: Unable to change points for a user! This may have been caused by a misformed user file";
      case 4:
        message = "WARNING: Error when decoding reaction! No valid reaction found";
      case 5:
        message = "WARNING: ";
      case 6:
        message = "WARNING: ";
      case 7:
        message = "WARNING: ";
      case 8:
        message = "WARNING: ";
      case 9:
        message = "WARNING: An unknown error occurred!";
      default:
    }
    try {
      FileWriter fw =
          new FileWriter(
              Main.logFileLocation + ("log_" + getRunNumber()) + ".log",
              true); // the true will append the new data
      fw.write("\n" + message); // appends the string to the file
      fw.close();
    } catch (IOException ioe) {
      System.err.println("IOException: " + ioe.getMessage());
    }
  }
}
