package com.raikuman.troubleclub.club.voice;

import com.raikuman.botutilities.configs.ConfigIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Handles scheduling voice connections on a constant basis
 *
 * @version 1.0 2023-23-01
 * @since 1.0
 */
public class VoiceScheduler {

	public static final Logger logger = LoggerFactory.getLogger(VoiceScheduler.class);

	/**
	 * Schedule voice connections using config values at random times within the hour
	 */
	public static void scheduleVoiceConnections() {
		VoiceManager.getInstance().handleCharacterVoice();

		SecureRandom rand = new SecureRandom();

		int minSched, additiveToMinShed;
		try {
			minSched = Integer.parseInt(
				ConfigIO.readConfig("voice", "minSched")
			);

			additiveToMinShed = Integer.parseInt(
				ConfigIO.readConfig("voice", "addMinSched")
			);
		} catch (NumberFormatException e) {
			logger.error("Could not get minimum and additive scheduling configs");
			return;
		}

		String format = "yyyy-MM-dd HH:mm:ss";
		LocalDateTime generatedTime = LocalDateTime.now().plusSeconds(rand.nextInt(additiveToMinShed) + minSched);
		Date date;
		try {
			date = new SimpleDateFormat(format)
				.parse(DateTimeFormatter.ofPattern(format).format(generatedTime));
		} catch (ParseException e) {
			logger.error("Could not parse date for timer: " + generatedTime);
			return;
		}

		if (date == null)
			return;

		//Now create the time and schedule it
		Timer timer = new Timer();

		//Use this if you want to execute it once
		timer.schedule(new RunVoiceTask(), date);
	}
}

/**
 * Handles running the voice connection when scheduled, recursively
 *
 * @version 1.0 2023-23-01
 * @since 1.0
 */
class RunVoiceTask extends TimerTask {
	public void run() {
		VoiceScheduler.scheduleVoiceConnections();
	}
}