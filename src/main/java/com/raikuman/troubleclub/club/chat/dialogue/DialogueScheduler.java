package com.raikuman.troubleclub.club.chat.dialogue;

import com.raikuman.botutilities.configs.ConfigIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Handles scheduling dialogues on restart
 *
 * @version 1.1 2023-23-01
 * @since 1.0
 */
public class DialogueScheduler {

	public static final Logger logger = LoggerFactory.getLogger(DialogueScheduler.class);


	/**
	 * Checks for dialogue scheduling by settings dialogue dates to run dialogue scripts at random times
	 */
	public static void checkDialogueScheduling() {
		// Check for selected date
		String configDate = ConfigIO.readConfig("dialoguescheduler", "selectdate");
		// Calculate a select date
		LocalDate selectedDate;
		if (configDate == null) {
			selectedDate = generateSelectDate(LocalDate.now().plusDays(1));
		} else {
			try {
				selectedDate = LocalDate.parse(configDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			} catch (DateTimeParseException e) {
				logger.error("Could not parse date from date config: " + configDate);
				return;
			}

			// Check if parsed date is before today
			if (selectedDate.isBefore(LocalDate.now())) {
				selectedDate = LocalDate.now();

				ConfigIO.overwriteConfig("dialoguescheduler", "selectdate", selectedDate.toString());
				return;
			}

			// Check if there are no more hours left to schedule a dialogue today
			if (LocalTime.now().isAfter(LocalTime.of(22, 59, 59))) {
				selectedDate = LocalDate.now().plusDays(1);

				ConfigIO.overwriteConfig("dialoguescheduler", "selectdate", selectedDate.toString());
				return;
			}
		}

		// Check if today is the selected date
		if (!LocalDate.now().equals(selectedDate))
			return;

		// Generate time to schedule the dialogue
		SecureRandom rand = new SecureRandom();
		String generatedTime = String.format("%02d", generateHour()) + ":" + String.format("%02d",
			rand.nextInt(59)) + ":00";

		// Parse date for timer
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date;
		try {
			date = dateFormatter.parse(LocalDate.now() + " " + generatedTime);
		} catch (ParseException e) {
			logger.error("Could not parse date for timer: " + LocalDate.now() + " " + generatedTime);
			return;
		}

		if (date == null)
			return;

		//Now create the time and schedule it
		Timer timer = new Timer();

		//Use this if you want to execute it once
		timer.schedule(new RunDialogueTask(), date);

		// Generate new select date
		int minDays;
		try {
			minDays = Integer.parseInt(ConfigIO.readConfig("dialoguescheduler", "minScheduledDays"));
		} catch (NumberFormatException e) {
			logger.error("Could not get minimum days from config");
			return;
		}

		// Check for random day bypass
		boolean ignoreRandom = Boolean.parseBoolean(ConfigIO.readConfig("dialoguescheduler",
			"bypassRandomAddedDays"));

		if (ignoreRandom)
			selectedDate = LocalDate.now().plusDays(minDays);
		else
			selectedDate = generateSelectDate(LocalDate.now().plusDays(minDays));

		// Write new selected date to config
		ConfigIO.overwriteConfig("dialoguescheduler", "selectdate", selectedDate.toString());

		// Log scheduled dialogue
		dialogueLogger(date);
	}

	/**
	 * Generates a random hour given a map of weighted valid hours
	 * @return The hour randomly generated
	 */
	private static int generateHour() {
		List<Integer> keyList = new ArrayList<>(getHourMap().keySet());

		SecureRandom rand = new SecureRandom();
		Collections.shuffle(keyList);

		DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
		int retrievedHour = -1;
		while (retrievedHour == -1) {
			int targetKey = keyList.get(rand.nextInt(keyList.size()));
			if (rand.nextDouble() > Math.abs(getHourMap().get(targetKey) - 1)) {
				try {
					LocalTime selectedTime = LocalTime.parse(
						String.format("%02d", targetKey) + ":00:00",
						timeFormat);

					if (selectedTime.isAfter(LocalTime.now()))
						retrievedHour = targetKey;
				} catch (DateTimeParseException e) {
					logger.error("Could not parse time");
				}
			}
		}

		return retrievedHour;
	}

	/**
	 * Generate a select date to schedule the dialogue on
	 * @param targetDate The target date specified to generate the select date
	 * @return The generated select date
	 */
	private static LocalDate generateSelectDate(LocalDate targetDate) {
		SecureRandom rand = new SecureRandom();
		int selectAfterNextDays = 5, numDaysToAdd = rand.nextInt(3);

		boolean selected = false;
		int trials = 0, weightDivMultiplier = 2, weight = 1, weightDivider = 3 - numDaysToAdd;
		double currentPercent;
		while (!selected) {
			currentPercent = rand.nextDouble();

			// Increase weight effectiveness over
			if (trials > weightDivMultiplier)
				if (weightDivider > 1)
					weightDivider--;

			// Calculate threshold
			double threshold = Math.pow(0.6, (double) weight / weightDivider);
			if (currentPercent > threshold)
				selected = true;

			// Increase weight
			if (!selected) {
				trials++;
				weight++;
			}
		}

		return targetDate.plusDays(selectAfterNextDays + numDaysToAdd + trials);
	}

	/**
	 * A map of hours with weighted values
	 * @return The map of hours
	 */
	private static HashMap<Integer, Double> getHourMap() {
		return new HashMap<>()
		{{
			put(9, 0.03);
			put(10, 0.03);
			put(11, 0.03);
			put(12, 0.06);
			put(13, 0.08);
			put(14, 0.09);
			put(15, 0.08);
			put(16, 0.07);
			put(17, 0.06);
			put(18, 0.06);
			put(19, 0.07);
			put(20, 0.09);
			put(21, 0.07);
			put(22, 0.08);
			put(23, 0.1);
		}};
	}

	/**
	 * Logs the date that is scheduled to run the next dialogue
	 * @param date The date to print to a file
	 */
	private static void dialogueLogger(Date date) {
		// Append random dialogue to completed dialogue file
		File file = new File("resources/dialogue/dialog.txt");
		if (!file.exists()) {
			try {
				if (!file.createNewFile())
					return;

			} catch (IOException e) {
				logger.error("Could not create completed dialogue file");
				return;
			}
		}

		// Reset completed dialogues if all dialogues have been completed
		try(FileWriter fw = new FileWriter(file, true);
		    BufferedWriter bw = new BufferedWriter(fw);
		    PrintWriter out = new PrintWriter(bw))
		{
			out.println(date);
		} catch (IOException e) {
			logger.error("Could not print to file");
		}
	}
}

/**
 * Handles running the dialogue when scheduled
 *
 * @version 1.0 2023-22-01
 * @since 1.0
 */
class RunDialogueTask extends TimerTask {
	public void run() {
		DialogueManager.getInstance().playDialogue();
	}
}
