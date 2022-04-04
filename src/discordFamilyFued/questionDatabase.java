package discordFamilyFued;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class questionDatabase {
	static File questions = new File(Main.questionFileLocation + "questions.txt");
	static File firstPlace = new File(Main.questionFileLocation + "firstPlace.txt");
	static File secondPlace = new File(Main.questionFileLocation + "secondPlace.txt");
	static File thirdPlace = new File(Main.questionFileLocation + "thirdPlace.txt");
	static File fourthPlace = new File(Main.questionFileLocation + "fourthPlace.txt");
	static File fifthPlace = new File(Main.questionFileLocation + "fifthPlace.txt");
	static File sixthPlace = new File(Main.questionFileLocation + "sixthPlace.txt");

	static String[] questionsArray = new String[getLineCount("questions.txt")];
	static String[] firstPlaceAnswers = new String[getLineCount("firstPlace.txt")];
	static String[] secondPlaceAnswers = new String[getLineCount("secondPlace.txt")];
	static String[] thirdPlaceAnswers = new String[getLineCount("thirdPlace.txt")];
	static String[] fourthPlaceAnswers = new String[getLineCount("fourthPlace.txt")];
	static String[] fifthPlaceAnswers = new String[getLineCount("fifthPlace.txt")];
	static String[] sixthPlaceAnswers = new String[getLineCount("sixthPlace.txt")];

	public questionDatabase() {
		questionsArray = populateArray("questions.txt");
		firstPlaceAnswers = populateArray("firstPlace.txt");
		secondPlaceAnswers = populateArray("secondPlace.txt");
		thirdPlaceAnswers = populateArray("thirdPlace.txt");
		fourthPlaceAnswers = populateArray("fourthPlace.txt");
		fifthPlaceAnswers = populateArray("fifthPlace.txt");
		sixthPlaceAnswers = populateArray("sixthPlace.txt");
	}

	public String getQuestion(int QuestionNumber) {
		String question = "placeholderQuestion";
		question = questionsArray[QuestionNumber];

		return question;
	}

	public String[] getAnswerAndValue(int QuestionNumber) {
		String[] answers = new String[6];
		answers[0] = firstPlaceAnswers[QuestionNumber];
		answers[1] = secondPlaceAnswers[QuestionNumber];
		answers[2] = thirdPlaceAnswers[QuestionNumber];
		answers[3] = fourthPlaceAnswers[QuestionNumber];
		answers[4] = fifthPlaceAnswers[QuestionNumber];
		answers[5] = sixthPlaceAnswers[QuestionNumber];
		return answers;
	}

	public String[] getAnswer(int QuestionNumber) {
		String[] answers = new String[6];

		String[] answerAndValue = firstPlaceAnswers[QuestionNumber].split(" ");
		answerAndValue[answerAndValue.length - 1] = "";
		answers[0] = String.join(" ", answerAndValue);

		answerAndValue = secondPlaceAnswers[QuestionNumber].split(" ");
		answerAndValue[answerAndValue.length - 1] = "";
		answers[1] = String.join(" ", answerAndValue);

		answerAndValue = thirdPlaceAnswers[QuestionNumber].split(" ");
		answerAndValue[answerAndValue.length - 1] = "";
		answers[2] = String.join(" ", answerAndValue);

		answerAndValue = fourthPlaceAnswers[QuestionNumber].split(" ");
		answerAndValue[answerAndValue.length - 1] = "";
		answers[3] = String.join(" ", answerAndValue);

		answerAndValue = fifthPlaceAnswers[QuestionNumber].split(" ");
		answerAndValue[answerAndValue.length - 1] = "";
		answers[4] = String.join(" ", answerAndValue);

		answerAndValue = sixthPlaceAnswers[QuestionNumber].split(" ");
		answerAndValue[answerAndValue.length - 1] = "";
		answers[5] = String.join(" ", answerAndValue);

		return answers;
	}

	public static Integer[] questionValue(int QuestionNumber) {
		int pointValue;
		Integer[] pointValues = new Integer[6];

		String[] answerAndValue = firstPlaceAnswers[QuestionNumber].split(" ");
		pointValue = Integer.parseInt(answerAndValue[answerAndValue.length - 1]);
		pointValues[0] = pointValue;

		answerAndValue = secondPlaceAnswers[QuestionNumber].split(" ");
		pointValue = Integer.parseInt(answerAndValue[answerAndValue.length - 1]);
		pointValues[1] = pointValue;

		answerAndValue = thirdPlaceAnswers[QuestionNumber].split(" ");
		pointValue = Integer.parseInt(answerAndValue[answerAndValue.length - 1]);
		pointValues[2] = pointValue;

		answerAndValue = fourthPlaceAnswers[QuestionNumber].split(" ");
		pointValue = Integer.parseInt(answerAndValue[answerAndValue.length - 1]);
		pointValues[3] = pointValue;

		answerAndValue = fifthPlaceAnswers[QuestionNumber].split(" ");
		pointValue = Integer.parseInt(answerAndValue[answerAndValue.length - 1]);
		pointValues[4] = pointValue;

		answerAndValue = sixthPlaceAnswers[QuestionNumber].split(" ");
		pointValue = Integer.parseInt(answerAndValue[answerAndValue.length - 1]);
		pointValues[5] = pointValue;

		return pointValues;
	}

	public String[] populateArray(String fileName) {
		String[] returnedString = new String[getLineCount("questions.txt")];
		int lineNumber = 0;

		try {
			for (String line : Files.readAllLines(Paths.get(Main.questionFileLocation + fileName),
					StandardCharsets.UTF_8)) {

				returnedString[lineNumber] = line;

				lineNumber++;

			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block

			e1.printStackTrace();
		}

		return returnedString;
	}

	public int getQuestionCount() {
		return questionsArray.length;
	}

	static public int getLineCount(String fileName) {

		int lineNumber = 0;

		try {
			for (String line : Files.readAllLines(Paths.get(Main.questionFileLocation + fileName),
					StandardCharsets.UTF_8)) {
				lineNumber++;

			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block

			e1.printStackTrace();
		}

		return lineNumber;
	}

}