package com.raikuman.troubleclub.club.statemanager;

import com.raikuman.botutilities.configs.ConfigIO;
import com.raikuman.troubleclub.club.utilities.CharacterNames;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.security.SecureRandom;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manages rescheduling task dates for interactions so that they don't overlap
 *
 * @version 1.0 2023-22-02
 * @since 1.0
 */
public class ScheduleConflictManager {

	private static ScheduleConflictManager instance = null;
	private final HashMap<CharacterStates, Date> scheduledDates;

	public ScheduleConflictManager() {
		// Initialize hashmap with keys
		scheduledDates = new LinkedHashMap<>();
		for (CharacterStates characterState : CharacterStates.values()) {
			scheduledDates.put(
				characterState,
				null
			);
		}
	}

	public static ScheduleConflictManager getInstance() {
		if (instance == null)
			instance = new ScheduleConflictManager();

		return instance;
	}

	/**
	 * Manage the input date given the character state interaction so that it does not conflict with other
	 * task dates
	 * @param characterState The character state interaction to check with
	 * @param scheduledDate The input schedule date for the character state
	 * @return The managed date to use for scheduling
	 */
	public Date manageDate(CharacterStates characterState, Date scheduledDate) {
		HashMap<CharacterStates, Date> checkMap = new LinkedHashMap<>(scheduledDates);
		checkMap.remove(characterState);

		long stateMinutes = (getRescheduleMap().get(characterState) * 60 * 1000);
		Date min = new Date(scheduledDate.getTime() - stateMinutes);
		Date max = new Date(scheduledDate.getTime() + stateMinutes);
		Date finalDate, foundDate = null;

		for (Map.Entry<CharacterStates, Date> checkEntry : checkMap.entrySet()) {
			if (checkEntry.getValue() == null)
				continue;

			if (checkEntry.getValue().after(min) && checkEntry.getValue().before(max)) {
				if (foundDate == null)
					foundDate = checkEntry.getValue();

				if (checkEntry.getValue().equals(foundDate) || checkEntry.getValue().after(foundDate))
					foundDate = checkEntry.getValue();
			}
		}

		if (foundDate == null) {
			scheduledDates.put(characterState, scheduledDate);
			finalDate = scheduledDate;
		} else {
			// Move date a random amount of minutes based on the reschedule map
			SecureRandom rand = new SecureRandom();
			long randomMinutes =
				(long) (rand.nextInt(getRescheduleMap().get(characterState) / 2) + getRescheduleMap().get(characterState) / 2) * 60 * 1000;

			Date newDate = new Date(foundDate.getTime() + randomMinutes);
			scheduledDates.put(characterState, newDate);
			finalDate = newDate;
		}

		logSchedulingManager();

		return finalDate;
	}

	/**
	 * Log scheduling manager
	 */
	private void logSchedulingManager() {
		String textChannelId = ConfigIO.readConfig("state", "loggingchannelid");
		if (textChannelId == null)
			return;

		TextChannel textChannel =
			CharacterStateManager.getInstance().getCharacterStateMap()
				.get(CharacterNames.SUU).getJda().getTextChannelById(textChannelId);
		if (textChannel == null)
			return;

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("```md\n");

		int counter = 1;
		LocalTime time = LocalTime.now();
		for (Map.Entry<CharacterStates, Date> scheduleEntry : scheduledDates.entrySet()) {
			// Number
			stringBuilder.append(counter);
			stringBuilder.append(". ");

			// Schedule type
			stringBuilder
				.append("[")
				.append(scheduleEntry.getKey())
				.append("]");

			// Scheduled Date
			stringBuilder
				.append(" ")
				.append(scheduleEntry.getValue())
				.append(" ");

			// Time
			stringBuilder
				.append(time.getHour())
				.append(":")
				.append(time.getMinute())
				.append(":")
				.append(time.getSecond());
			stringBuilder.append("\n");

			counter++;
		}

		stringBuilder.append("```");

		textChannel.sendMessage(stringBuilder).queue();
	}

	/**
	 * A map of minute thresholds to check for scheduling conflicts
	 * @return The map of scheduling minute thresholds
	 */
	private static HashMap<CharacterStates, Integer> getRescheduleMap() {
		return new HashMap<>()
		{{
			put(CharacterStates.DIALOGUE, 15);
			put(CharacterStates.STATUS, 30);
			put(CharacterStates.VOICE, 10);
			put(CharacterStates.IDLE, 5);
		}};
	}
}
