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
  questionDatabase questionDatabase;
  long botGameMessageID;
  ArrayList<String> Roster;
  ServerInstance server;
  LogSystem logger;

  public Game(ServerInstance thisServer) {
    this.logger = new LogSystem(Main.getRunNumber());
    this.server = thisServer;
    this.Roster = server.Roster;
    questionDatabase = new questionDatabase();
    botGameMessageID = -1;
  }

  public void startNewGame(MessageChannel channel, int[] userAnswers1) {
    server.setRoundNumber(server.getRoundNumber() + 1);
    final ArrayList<String> Roster = this.Roster;
    logger.Log(
        "New round in server " + server.getName() + " with roster: " + String.join(", ", Roster));
    if (server.getRoundNumber() == 0) {
      for (int i = 0; Roster.size() > i; ) {
        makeUserFile(Roster.get(i));
        i++;
      }
    }

    if (server.getRoundNumber() == 5) {
      endGame(channel);
      return;
    }
    newRound(Roster, channel, userAnswers1);
  }

  public void newRound(ArrayList<String> Roster, MessageChannel channel, int[] userAnswers1) {
    Random rand = new Random();
    long seed = System.nanoTime();
    int randomNumber = rand.nextInt(questionDatabase.getLineCount("questions.txt"));
    String[] answers = questionDatabase.getAnswer(randomNumber);
    Integer[] answerValue = questionDatabase.questionValue(randomNumber);
    Collections.shuffle(Arrays.asList(answers), new Random(seed));
    Collections.shuffle(Arrays.asList(answerValue), new Random(seed));
    Integer[] placementOfAnswer = new Integer[6];
    for (int i = 0; placementOfAnswer.length > i; ) {
      placementOfAnswer[i] = i;
      i++;
    }
    Collections.shuffle(Arrays.asList(placementOfAnswer), new Random(seed));
    logger.Log("Question " + randomNumber + " was selected in server: " + server.getName());
    channel.sendMessage("**Question Number " + randomNumber + "**").queue();
    sendMessageWithReactions(
        channel,
        questionDatabase.getQuestion(randomNumber)
            + "\n"
            + "**1. **"
            + answers[0]
            + "\n"
            + "**2. **"
            + answers[1]
            + "\n"
            + "**3. **"
            + answers[2]
            + "\n"
            + "**4. **"
            + answers[3]
            + "\n"
            + "**5. **"
            + answers[4]
            + "\n"
            + "**6. **"
            + answers[5],
        "\u0031\ufe0f\u20e3",
        "\u0032\ufe0f\u20e3",
        "\u0033\ufe0f\u20e3",
        "\u0034\ufe0f\u20e3",
        "\u0035\ufe0f\u20e3",
        "\u0036\ufe0f\u20e3");

    while (server.getGameState() != 2) {
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        logger.Log(e.toString());
      }
    }
    logger.Log("All answers collected in server: " + server.getName());
    channel.sendMessage("**The answers are in! **").queue();
    int[] userAnswers = userAnswers1;
    String[] answers2 = questionDatabase.getAnswerAndValue(randomNumber);
    channel
        .sendMessage(
            "\n"
                + "**1. **"
                + answers2[0]
                + "\n"
                + "**2. **"
                + answers2[1]
                + "\n"
                + "**3. **"
                + answers2[2]
                + "\n"
                + "**4. **"
                + answers2[3]
                + "\n"
                + "**5. **"
                + answers2[4]
                + "\n"
                + "**6. **"
                + answers2[5]
                + "\n")
        .queue();
    for (int i = 0; Roster.size() > i; ) {
      addUserPoints(Roster.get(i), answerValue[userAnswers[i]]);
      channel
          .sendMessage(
              Roster.get(i)
                  + " chose"
                  + " **"
                  + (placementOfAnswer[userAnswers[i]] + 1)
                  + ".** "
                  + "\""
                  + answers[userAnswers[i]]
                  + "\""
                  + " They earned "
                  + answerValue[userAnswers[i]]
                  + " points. Total: "
                  + getUserPoints(Roster.get(i))
                  + "!")
          .queue();
      i++;
    }

    server.setGameState(3);
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      logger.Log(e.toString());
    }

    if (server.getRoundNumber() >= 2) {
      endGame(channel);
    } else {
      server.makeNextRound(channel);
    }

    return;
  }

  @SuppressWarnings({"rawtypes", "deprecation"})
  public void endGame(MessageChannel channel) {
    String endScore = "";
    SortedMap<Integer, String> tm1 = new TreeMap<Integer, String>();

    for (int i = 0; Roster.size() > i; ) {
      tm1.put(getUserPoints(Roster.get(i)), Roster.get(i));

      i++;
    }
    for (Map.Entry mapElement : tm1.entrySet()) {
      int key = (int) mapElement.getKey();
      String value = (String) mapElement.getValue();
      endScore = endScore + value + " earned " + key + " points! " + "\n";
    }
    channel
        .sendMessage(
            "**Thanks for playing!" + "\n" + endScore + "You can start a new game with !new.**")
        .queue();
    server.setGameState(-1);
    Thread thread = Thread.currentThread();
    thread.stop();
  }

  @SuppressWarnings("deprecation")
  public void endGame() {
    logger.Log("WARNING: Game was stopped unexpectedly in server " + server.getName() + "!");
    server.setGameState(-1);
    Thread thread = Thread.currentThread();
    thread.stop();
  }

  public void sendMessageWithReactions(MessageChannel channel, String embed, String... reactions) {
    logger.Log("Sucessfully sent the question message in server: " + server.getName());
    channel
        .sendMessage(embed)
        .queue(
            msg -> {
              for (String reaction : reactions) {
                msg.addReaction(reaction).queue();
              }
              setbotGameMessageID(msg.getIdLong());
            });
  }

  public void makeUserFile(String user) {
    logger.Log("made new user " + user + " in server " + server.getName());
    try {
      FileOutputStream fos = new FileOutputStream(server.getFileLocation() + user + ".txt", false);
      String str = user + "\n" + "points=0";
      byte[] b = str.getBytes(); // converts string into bytes
      fos.write(b); // writes bytes into file
      fos.close(); // close the file
    } catch (Exception e) {
      logger.warn(1);
      logger.Log(e.toString());
    }
  }

  public int addUserPoints(String user, int points) {
    logger.Log("Trying to add points to user: " + user);
    int basePoints = getUserPoints(user);
    if (!userExists(user)) {
      logger.warn(2);
      return -2;
    }
    List<String> newLines = new ArrayList<>();
    try {
      for (String line :
          Files.readAllLines(
              Paths.get(server.getFileLocation() + user + ".txt"), StandardCharsets.UTF_8)) {
        if (line.contains("points=")) {
          newLines.add("points=" + (basePoints + points));
          logger.Log(
              "Successfully added points to user "
                  + user
                  + " "
                  + basePoints
                  + " -> "
                  + (basePoints + points)
                  + " in server: "
                  + server.getName());
        } else {
          newLines.add(line);
        }
      }
    } catch (IOException e) {
      logger.warn(1);
      logger.Log(e.toString());
      return -1;
    }
    try {
      Files.write(
          Paths.get(server.getFileLocation() + user + ".txt"), newLines, StandardCharsets.UTF_8);
      return 1;
    } catch (IOException e) {
      logger.warn(1);
      logger.Log(e.toString());
      return -1;
    }
  }

  public int getUserPoints(String user) {
    if (!userExists(user)) {
      logger.warn(2);
    }

    try {
      for (String line :
          Files.readAllLines(
              Paths.get(server.getFileLocation() + user + ".txt"), StandardCharsets.UTF_8)) {
        if (line.contains("points=")) {
          if (!(Integer.parseInt(line.replaceAll("[^0-9]*", "")) >= 0)) {
            logger.warn(3);
          }

          return Integer.parseInt(line.replaceAll("[^0-9]*", ""));
        }
      }
    } catch (IOException e1) {
      logger.warn(1);
      e1.printStackTrace();
    }
    logger.warn(9);
    return -1;
  }

  public boolean userExists(String user) {
    File f = new File(server.getFileLocation());
    String[] pathnames = f.list();
    user = user.replaceAll(".txt", "");
    if (Arrays.asList(pathnames).contains(user.toLowerCase() + ".txt")) {
      return true;
    }
    return false;
  }

  public long getbotGameMessageID() {
    return botGameMessageID;
  }

  public void setbotGameMessageID(long ID) {
    botGameMessageID = ID;
  }
}
