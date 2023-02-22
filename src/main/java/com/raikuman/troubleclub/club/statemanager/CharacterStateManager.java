package com.raikuman.troubleclub.club.statemanager;

import com.raikuman.botutilities.configs.ConfigIO;
import com.raikuman.troubleclub.club.statemanager.managers.dialogue.DialogueStateManager;
import com.raikuman.troubleclub.club.statemanager.managers.dialogue.DialogueUtilities;
import com.raikuman.troubleclub.club.statemanager.managers.status.StatusStateManager;
import com.raikuman.troubleclub.club.statemanager.managers.voice.IdleStateManager;
import com.raikuman.troubleclub.club.utilities.CharacterNames;
import com.raikuman.troubleclub.club.utilities.JDAFinder;
import com.raikuman.troubleclub.club.statemanager.managers.voice.VoiceStateManager;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Manages the states of characters when doing dialogue, status, idle, or muting
 *
 * @version 1.0 2023-19-02
 * @since 1.0
 */
public class CharacterStateManager {

	private static final Logger logger = LoggerFactory.getLogger(CharacterStateManager.class);
	private static CharacterStateManager instance = null;
	private final HashMap<CharacterNames, CharacterStateObject> characterStateMap;

	public CharacterStateManager() {
		HashMap<CharacterNames, CharacterStateObject> stateMap = new LinkedHashMap<>();
		for (CharacterNames characterName : CharacterNames.values()) {
			stateMap.put(
				characterName,
				new CharacterStateObject(JDAFinder.getInstance().getJDA(characterName))
			);
		}

		this.characterStateMap = stateMap;
	}

	public static CharacterStateManager getInstance() {
		if (instance == null)
			instance = new CharacterStateManager();

		return instance;
	}

	public HashMap<CharacterNames, CharacterStateObject> getCharacterStateMap() {
		return characterStateMap;
	}

	/**
	 * Main method to begin scheduling the character states
	 */
	public void handleScheduling() {
		// Instantiate all required managers to being scheduling their respective states
		handleDialogue();
		handleStatus();
		handleIdling();
		handleVoice();
	}

	/**
	 * Handle scheduling the dialogue on the given day, checking for the date it should be scheduled and
	 * automatically handling rescheduling a new date when prompted to schedule a dialogue
	 */
	private void handleDialogue() {
		// Check to run dialogue
		if (Boolean.parseBoolean(ConfigIO.readConfig("troubleclub/dialogue", "disableDialogue"))) {
			logger.info("Dialogue disabled");
			return;
		}

		// Check for selected date
		LocalDate selectedDate = DialogueUtilities.getSelectDate();
		if (selectedDate == null)
			return;

		// Check if today is the selected date
		if (!LocalDate.now().equals(selectedDate))
			return;

		// Get date to schedule the dialogue
		Date scheduleDate = DialogueUtilities.generateTaskDate();
		if (scheduleDate == null)
			return;

		// Check for conflicts
		scheduleDate = ScheduleConflictManager.getInstance().manageDate(CharacterStates.DIALOGUE, scheduleDate);

		// Schedule dialogue
		new Timer().schedule(new RunDialogueTask(), scheduleDate);
		// Log scheduled dialogue
		DialogueUtilities.dialogueLogger(scheduleDate);

		// Generate new selected date
		LocalDate newSelectedDate = DialogueUtilities.generateSelectDate(LocalDate.now());
		if (newSelectedDate == null)
			return;

		// Write new selected date to config
		ConfigIO.overwriteConfig("troubleclub/dialogue", "selectdate", newSelectedDate.toString());

		logScheduling(scheduleDate, CharacterStates.DIALOGUE);
	}

	/**
	 * Handle recursively scheduling the status states with the given config parameters
	 */
	protected void handleStatus() {
		StatusStateManager.getInstance().updateStatusesState(false);
		Date date = recursiveScheduling(new RunStatusTask(),
			"troubleclub/status",
			"lowerBoundMins",
			"addedUpperBoundMins",
			CharacterStates.STATUS);

		logScheduling(date, CharacterStates.STATUS);
	}

	/**
	 * Handle recursively scheduling the voice states with the given config parameters
	 */
	protected void handleVoice() {
		Date date = recursiveScheduling(new RunVoiceTask(),
			"troubleclub/voice",
			"lowerBoundMins",
			"addedUpperBoundMins",
			CharacterStates.VOICE);

		logScheduling(date, CharacterStates.VOICE);
	}

	/**
	 * Handle recursively scheduling the idling states with the given config parameters
	 */
	protected void handleIdling() {
		Date date = recursiveScheduling(new RunIdlingTask(),
			"troubleclub/voice",
			"idlingLowerBoundMins",
			"idlingAddedUpperBoundMins",
			CharacterStates.IDLE);

		logScheduling(date, CharacterStates.IDLE);
	}

	/**
	 * Recursively schedule tasks for the given character state interaction throughout the day
	 * @param timerTask The timer task object to schedule
	 * @param configFile The config file to read the recursive bounds from
	 * @param lowerBoundConfig The name of the lower bound config
	 * @param upperBoundConfig The name of the upper bound config
	 * @param characterState The character state interaction to handle scheduling for
	 * @return The date that the character state interaction was scheduled for
	 */
	private Date recursiveScheduling(TimerTask timerTask, String configFile, String lowerBoundConfig,
		String upperBoundConfig, CharacterStates characterState) {
		SecureRandom rand = new SecureRandom();
		int minSched, additiveToMinSched;
		try {
			minSched = Integer.parseInt(
				ConfigIO.readConfig(configFile, lowerBoundConfig)
			);

			additiveToMinSched = Integer.parseInt(
				ConfigIO.readConfig(configFile, upperBoundConfig)
			);
		} catch (NumberFormatException e) {
			logger.error("Could not get minimum and additive scheduling configs");
			return null;
		}

		String dateFormat = "yyyy-MM-dd HH:mm:ss";
		LocalDateTime generatedTime = LocalDateTime.now().plusMinutes(
			rand.nextInt(additiveToMinSched) + minSched
		);
		Date date;
		try {
			date = new SimpleDateFormat(dateFormat)
				.parse(DateTimeFormatter.ofPattern(dateFormat).format(generatedTime));
		} catch (ParseException e) {
			logger.error("Could not parse date for timer: " + generatedTime);
			return null;
		}

		date = ScheduleConflictManager.getInstance().manageDate(characterState, date);

		// Schedule voice connection
		new Timer().schedule(timerTask, date);

		return date;
	}

	/**
	 * Log scheduling
	 * @param scheduledDate The date that the character state interaction was scheduled for
	 * @param scheduleType The character state interaction that was scheduled
	 */
	private void logScheduling(Date scheduledDate, CharacterStates scheduleType) {
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
		stringBuilder.append("[SCHEDULING]");
		stringBuilder
			.append("[")
			.append(scheduleType)
			.append("]");

		// Scheduled Date
		stringBuilder
			.append(" ")
			.append(scheduledDate)
			.append(" ");

		// Time
		LocalTime time = LocalTime.now();
		stringBuilder
			.append(time.getHour())
			.append(":")
			.append(time.getMinute())
			.append(":")
			.append(time.getSecond());
		stringBuilder.append("\n");

		stringBuilder.append("```");

		textChannel.sendMessage(stringBuilder).queue();
	}
}

/**
 * TimerTask for scheduling voice states
 *
 * @version 1.0 2023-22-02
 * @since 1.0
 */
class RunVoiceTask extends TimerTask {

	@Override
	public void run() {
		VoiceStateManager.getInstance().updateConnectionsState();
		CharacterStateManager.getInstance().handleVoice();
	}
}

/**
 * TimerTask for scheduling status states
 *
 * @version 1.0 2023-22-02
 * @since 1.0
 */
class RunStatusTask extends TimerTask {

	@Override
	public void run() {
		CharacterStateManager.getInstance().handleStatus();
	}
}

/**
 * TimerTask for scheduling dialogue states
 *
 * @version 1.0 2023-22-02
 * @since 1.0
 */
class RunDialogueTask extends TimerTask {

	@Override
	public void run() {
		DialogueStateManager.getInstance().updateDialogueState();
	}
}

/**
 * TimerTask for scheduling idling states
 *
 * @version 1.0 2023-22-02
 * @since 1.0
 */
class RunIdlingTask extends TimerTask {

	@Override
	public void run() {
		IdleStateManager.getInstance().updateIdlingState();
		CharacterStateManager.getInstance().handleIdling();
	}
}