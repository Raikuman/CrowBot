package com.raikuman.troubleclub.club.statemanager.managers.dialogue;

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
 * Utility functions to help manage the dialogue state
 *
 * @version 1.0 2023-19-02
 * @since 1.0
 */
public class DialogueUtilities {

	private static final Logger logger = LoggerFactory.getLogger(DialogueUtilities.class);

	/**
	 * Get the selected date to run the dialogue on
	 * @return The LocalDate to run the dialogue
	 */
	public static LocalDate getSelectDate() {
		// Check for a selected date
		String configDate = ConfigIO.readConfig("troubleclub/dialogue", "selectdate");

		LocalDate selectedDate;
		if (configDate == null) {
			selectedDate = generateSelectDate(LocalDate.now());
		} else {
			// Parse config date
			try {
				selectedDate = LocalDate.parse(configDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			} catch (DateTimeParseException e) {
				logger.error("Could not parse date from date config: " + configDate);
				return null;
			}

			// Check if selected date is before today, then set today as the selected date
			if (selectedDate.isBefore(LocalDate.now())) {
				selectedDate = LocalDate.now();
				ConfigIO.overwriteConfig("troubleclub/dialogue", "selectdate", selectedDate.toString());
				return selectedDate;
			}

			// Check if it is too late to schedule a dialogue, set selected date to tomorrow
			if (LocalTime.now().isAfter(LocalTime.of(22, 59, 59))) {
				selectedDate = LocalDate.now().plusDays(1);
				ConfigIO.overwriteConfig("troubleclub/dialogue", "selectdate", selectedDate.toString());
				return selectedDate;
			}
		}

		return selectedDate;
	}

	/**
	 * Generate a selected date after the given target date
	 * @param targetDate The date to set a selected date after
	 * @return The generated LocalDate
	 */
	public static LocalDate generateSelectDate(LocalDate targetDate) {
		SecureRandom rand = new SecureRandom();
		int minSchedDays, maxRNGDays, rngDays;
		boolean bypassRNG;

		// Parse variables from config
		try {
			minSchedDays = Integer.parseInt(
				ConfigIO.readConfig("troubleclub/dialogue", "minimumScheduledDays")
			);

			maxRNGDays = Integer.parseInt(
				ConfigIO.readConfig("troubleclub/dialogue", "maxRNGDays")
			);

			rngDays = rand.nextInt(maxRNGDays);

			bypassRNG = Boolean.parseBoolean(
				ConfigIO.readConfig("troubleclub/dialogue", "bypassRNGDays")
			);
		} catch (NumberFormatException e) {
			logger.error("Could not parse date generation variables from config");
			return null;
		}

		boolean selected = false;
		int trials = 0, weightDivMultiplier = 2, weight = 1, weightDivider = maxRNGDays - rngDays;
		double currentPercent;
		while (!selected) {
			currentPercent = rand.nextDouble();

			// Increase weight effectiveness after number of trials
			if (trials > weightDivMultiplier)
				if (weightDivider > 1)
					weightDivider--;

			// Calculate threshold
			double threshold = Math.pow(0.6, (double) weight / weightDivider);
			if (currentPercent > threshold)
				selected = true;

			// Increase weight if no date was selected
			if (!selected) {
				trials++;
				weight++;
			}
		}

		if (bypassRNG)
			return targetDate.plusDays(minSchedDays);
		else
			return targetDate.plusDays(minSchedDays + rngDays + trials);
	}

	/**
	 * Generate the date to run the dialogue task on
	 * @return The Date to run the dialogue task
	 */
	public static Date generateTaskDate() {
		// Generate hour
		SecureRandom rand = new SecureRandom();
		List<Integer> keyList = new ArrayList<>(getHourMap().keySet());
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

		// Generate date
		String generatedTime = String.format("%02d", retrievedHour) + ":" +
			String.format("%02d", rand.nextInt(59)) + ":00";

		// Parse date for task
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return dateFormatter.parse(LocalDate.now() + " " + generatedTime);
		} catch (ParseException e) {
			logger.error("Could not parse date for timer: " + LocalDate.now() + " " + generatedTime);
			return null;
		}
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
	public static void dialogueLogger(Date date) {
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
