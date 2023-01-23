package com.raikuman.troubleclub.club.chat.dialogue;

import com.raikuman.botutilities.configs.ConfigIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class DialogueScheduler {

	public static final Logger logger = LoggerFactory.getLogger(DialogueScheduler.class);

	public static void checkDialogueSchedulingTesting() {
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

			// Check if parsed date is today or before
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

		System.out.println("GENERATE TIME: " + generatedTime);

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

		System.out.println("SCHEDULED DATE" + date);
	}

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

			// Check if parsed date is today or before
			if (selectedDate.isBefore(LocalDate.now())) {
				selectedDate = generateSelectDate(LocalDate.now().plusDays(1));

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
	}

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
}

class RunDialogueTask extends TimerTask {
	public void run() {
		DialogueManager.getInstance().playDialogue();
	}
}
