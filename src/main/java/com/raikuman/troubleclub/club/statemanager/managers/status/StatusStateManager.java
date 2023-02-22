package com.raikuman.troubleclub.club.statemanager.managers.status;

import com.raikuman.botutilities.configs.ConfigIO;
import com.raikuman.troubleclub.club.statemanager.managers.voice.VoiceStateManager;
import com.raikuman.troubleclub.club.statemanager.managers.status.objects.StatusCombinedObject;
import com.raikuman.troubleclub.club.utilities.CharacterNames;
import com.raikuman.troubleclub.club.statemanager.CharacterStateManager;
import com.raikuman.troubleclub.club.statemanager.CharacterStateObject;
import com.raikuman.troubleclub.club.statemanager.StatusTypes;
import com.raikuman.troubleclub.club.statemanager.JSONInteractionLoader;
import com.raikuman.troubleclub.club.statemanager.managers.status.objects.StatusCombinedArray;
import com.raikuman.troubleclub.club.statemanager.managers.status.objects.StatusObject;
import kotlin.Pair;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.Presence;

import java.security.SecureRandom;
import java.time.LocalTime;
import java.util.*;

/**
 * Manages setting the status for the character bots
 *
 * @version 1.0 2023-22-02
 * @since 1.0
 */
public class StatusStateManager {

	private static StatusStateManager instance = null;
	private EnumMap<StatusTypes, HashMap<CharacterNames, List<String>>> statusObjectMap;
	private List<StatusCombinedObject> combinedObjectList;
	private final HashMap<CharacterNames, Boolean> groupStatus;

	public StatusStateManager() {
		updateStatusObjects();
		this.groupStatus = new LinkedHashMap<>();
		for (CharacterNames characterName : CharacterNames.values()) {
			groupStatus.put(
				characterName,
				false
			);
		}
	}

	public static StatusStateManager getInstance() {
		if (instance == null)
			instance = new StatusStateManager();

		return instance;
	}

	/**
	 * Returns a hashmap of the online status for each character bot
	 * @return The online status hashmap
	 */
	public HashMap<CharacterNames, OnlineStatus> getStatusMap() {
		HashMap<CharacterNames, OnlineStatus> characterStatusMap = new LinkedHashMap<>();
		for (Map.Entry<CharacterNames, CharacterStateObject> characterState :
			CharacterStateManager.getInstance().getCharacterStateMap().entrySet()) {

			characterStatusMap.put(
				characterState.getKey(),
				characterState.getValue().getJda().getPresence().getStatus()
			);
		}

		return characterStatusMap;
	}

	/**
	 * Update the group and individual statuses
	 */
	public void updateStatusObjects() {
		List<StatusObject> statusObjects = JSONInteractionLoader.loadInteractionObjects(StatusObject.class);
		EnumMap<StatusTypes, HashMap<CharacterNames, List<String>>> statusObjectMap = new EnumMap<>(StatusTypes.class);

		if (statusObjects != null) {
			for (StatusObject statusObject : statusObjects)
				statusObjectMap.put(statusObject.type, statusObject.statusMap);

			this.statusObjectMap = statusObjectMap;
		}

		List<StatusCombinedArray> combinedArrays =
			JSONInteractionLoader.loadInteractionObjects(StatusCombinedArray.class);
		if (combinedArrays == null)
			return;

		List<StatusCombinedObject> combinedObjects = new ArrayList<>();
		for (StatusCombinedArray combinedArray : combinedArrays)
			combinedObjects.addAll(combinedArray.combinedList);

		this.combinedObjectList = combinedObjects;
	}

	/**
	 * Main method to update character bot statuses
	 * @param bypassGroup Bypass setting a group status
	 */
	public void updateStatusesState(boolean bypassGroup) {
		// Check for group activity
		SecureRandom rand = new SecureRandom();
		if (rand.nextDouble() < 0.16 && !bypassGroup) {
			handleGroupStatus();
			return;
		}

		handleCharacterStatuses();
	}

	/**
	 * Handle setting the group status for character bots
	 */
	private void handleGroupStatus() {
		// Randomly generate a combined status
		SecureRandom rand = new SecureRandom();
		StatusCombinedObject combinedObject = combinedObjectList.get(rand.nextInt(combinedObjectList.size()));

		// Get required characters for the group status
		List<CharacterNames> requiredCharacters = new ArrayList<>();
		if (combinedObject.allRequired) {
			requiredCharacters = combinedObject.character;
		} else {
			while (requiredCharacters.size() < 2) {
				for (CharacterNames characterName : CharacterNames.values())
					if (rand.nextBoolean())
						requiredCharacters.add(characterName);
			}
		}

		HashMap<CharacterNames, Pair<OnlineStatus, Activity>> newStatusMap = new LinkedHashMap<>();
		HashMap<CharacterNames, Boolean> newVoiceMap = new LinkedHashMap<>();
		for (CharacterNames characterName : CharacterNames.values()) {
			boolean connect = true;
			OnlineStatus onlineStatus = OnlineStatus.ONLINE;
			Activity activity;
			if (requiredCharacters.contains(characterName)) {
				Activity.ActivityType activityType;
				switch (combinedObject.statusType) {
					case WATCHING:
						activityType = Activity.ActivityType.WATCHING;
						break;

					case LISTENING:
						activityType = Activity.ActivityType.LISTENING;
						break;

					case PLAYING:
						activityType = Activity.ActivityType.PLAYING;
						break;

					default:
						activityType = Activity.ActivityType.COMPETING;
				}

				// Handle edge case
				if (combinedObject.text.equals("Street races")) {
					onlineStatus = OnlineStatus.DO_NOT_DISTURB;
					activityType = Activity.ActivityType.COMPETING;
					connect = false;
				}

				activity = Activity.of(activityType, combinedObject.text);
				groupStatus.put(
					characterName,
					true
				);
			} else {
				// Check if status is new
				Activity currentActivity = CharacterStateManager.getInstance().getCharacterStateMap()
					.get(characterName).getJda().getPresence().getActivity();

				if (rand.nextBoolean() && currentActivity != null && !groupStatus.get(characterName))
					continue;

				// Randomly generate an OnlineStatus
				List<OnlineStatus> onlineStatusList = new ArrayList<>(Arrays.asList(OnlineStatus.values()));
				onlineStatusList.removeAll(Arrays.asList(
					OnlineStatus.OFFLINE,
					OnlineStatus.INVISIBLE,
					OnlineStatus.UNKNOWN
				));
				onlineStatus = onlineStatusList.get(rand.nextInt(onlineStatusList.size()));

				// Get random activity given OnlineStatus
				activity = retrieveRandomActivity(onlineStatus, characterName);
				groupStatus.put(
					characterName,
					false
				);
			}

			newStatusMap.put(
				characterName,
				new Pair<>(onlineStatus, activity)
			);

			newVoiceMap.put(
				characterName,
				connect
			);
		}

		// Check for only changes in voice map
		for (Map.Entry<CharacterNames, Boolean> originalConnectionEntry : VoiceStateManager.getInstance()
			.getConnectionMap().entrySet()) {
			// Check for characters that are being updated
			if (newVoiceMap.get(originalConnectionEntry.getKey()) == null)
				continue;

			if (originalConnectionEntry.getValue() == newVoiceMap.get(originalConnectionEntry.getKey()))
				newVoiceMap.remove(originalConnectionEntry.getKey());
		}

		updateStatuses(newStatusMap);

		VoiceStateManager.getInstance().updateConnections(newVoiceMap);
	}

	/**
	 * Handle setting individual character bot statuses
	 */
	private void handleCharacterStatuses() {
		HashMap<CharacterNames, Pair<OnlineStatus, Activity>> newStatusMap = new LinkedHashMap<>();

		// Handle updating new status map
		SecureRandom rand = new SecureRandom();
		for (CharacterNames characterName : CharacterNames.values()) {
			// Check if status is new
			Activity currentActivity = CharacterStateManager.getInstance().getCharacterStateMap()
				.get(characterName).getJda().getPresence().getActivity();

			if (rand.nextBoolean() && currentActivity != null && !groupStatus.get(characterName))
				continue;

			// Randomly generate an OnlineStatus
			List<OnlineStatus> onlineStatusList = new ArrayList<>(Arrays.asList(OnlineStatus.values()));
			onlineStatusList.removeAll(Arrays.asList(
				OnlineStatus.OFFLINE,
				OnlineStatus.INVISIBLE,
				OnlineStatus.UNKNOWN
			));
			OnlineStatus onlineStatus = onlineStatusList.get(rand.nextInt(onlineStatusList.size()));

			// Get random activity given OnlineStatus
			Activity activity = retrieveRandomActivity(onlineStatus, characterName);

			newStatusMap.put(
				characterName,
				new Pair<>(onlineStatus, activity)
			);

			groupStatus.put(
				characterName,
				false
			);
		}

		updateStatuses(newStatusMap);

		if (rand.nextDouble() < 0.35)
			VoiceStateManager.getInstance().updateConnectionsState();
	}

	/**
	 * Update the statuses of character bots given a status map of characters to update
	 * @param statusMap The map to update the presence of the character bots
	 */
	private void updateStatuses(HashMap<CharacterNames, Pair<OnlineStatus, Activity>> statusMap) {
		for (Map.Entry<CharacterNames, Pair<OnlineStatus, Activity>> statusEntry : statusMap.entrySet()) {
			Presence characterPresence = CharacterStateManager.getInstance().getCharacterStateMap()
				.get(statusEntry.getKey()).getJda().getPresence();

			characterPresence.setPresence(
				statusEntry.getValue().getFirst(),
				statusEntry.getValue().getSecond()
			);
		}

		logStatusChanges(statusMap);
	}

	/**
	 * Retrieve a random activity given the online status and the character bot
	 * @param onlineStatus The online status of the character bot
	 * @param characterName The name of the character bot
	 * @return The random activity retrieved from the status map given the parameters
	 */
	private Activity retrieveRandomActivity(OnlineStatus onlineStatus, CharacterNames characterName) {
		// Get applicable status type
		List<StatusTypes> lookTypes = new ArrayList<>(Arrays.asList(StatusTypes.values()));
		lookTypes.remove(StatusTypes.COMPETING);
		switch (onlineStatus) {
			case ONLINE:
				lookTypes.removeAll(Arrays.asList(
					StatusTypes.DO_NOT_DISTURB,
					StatusTypes.JOB
				));
				break;

			case IDLE:
				lookTypes.remove(StatusTypes.DO_NOT_DISTURB);
				break;

			case DO_NOT_DISTURB:
				lookTypes.remove(StatusTypes.NORMAL);
				break;
		}

		SecureRandom rand = new SecureRandom();
		String targetStatus = "";
		StatusTypes targetType = null;

		// Ensure character gets a status
		while (targetStatus.isEmpty()) {
			targetType = lookTypes.get(rand.nextInt(lookTypes.size()));
			List<String> characterStatus = statusObjectMap.get(targetType).get(characterName);
			targetStatus = characterStatus.get(rand.nextInt(characterStatus.size()));
		}

		// Return activity
		switch (targetType) {
			case PLAYING:
				return Activity.playing(targetStatus);

			case STREAMING:
				return Activity.streaming(targetStatus, "https://www.twitch.tv/suuperlunar");

			case LISTENING:
				return Activity.listening(targetStatus);

			case WATCHING:
				return Activity.watching(targetStatus);

			default:
				return Activity.competing(targetStatus);
		}
	}

	/**
	 * Handle updating character bots who disconnect from the voice channel while in a group status
	 * @param disconnectedCharacters The list of character bots to update the statuses of
	 */
	public void handleGroupStatusDisconnect(List<CharacterNames> disconnectedCharacters) {
		HashMap<CharacterNames, Pair<OnlineStatus, Activity>> newStatusMap = new LinkedHashMap<>();
		SecureRandom rand = new SecureRandom();

		for (CharacterNames characterName : disconnectedCharacters) {
			if (groupStatus.get(characterName)) {
				// Check if status is new
				Activity currentActivity = CharacterStateManager.getInstance().getCharacterStateMap()
					.get(characterName).getJda().getPresence().getActivity();

				if (rand.nextBoolean() && currentActivity != null && !groupStatus.get(characterName))
					continue;

				// Randomly generate an OnlineStatus
				List<OnlineStatus> onlineStatusList = new ArrayList<>(Arrays.asList(OnlineStatus.values()));
				onlineStatusList.removeAll(Arrays.asList(
					OnlineStatus.OFFLINE,
					OnlineStatus.INVISIBLE,
					OnlineStatus.UNKNOWN
				));
				OnlineStatus onlineStatus = onlineStatusList.get(rand.nextInt(onlineStatusList.size()));

				// Get random activity given OnlineStatus
				Activity activity = retrieveRandomActivity(onlineStatus, characterName);

				newStatusMap.put(
					characterName,
					new Pair<>(onlineStatus, activity)
				);

				groupStatus.put(
					characterName,
					false
				);
			}
		}

		updateStatuses(newStatusMap);
	}

	/**
	 * Log status updates
	 * @param statusMap The status map to log
	 */
	private void logStatusChanges(HashMap<CharacterNames, Pair<OnlineStatus, Activity>> statusMap) {
		if (statusMap.isEmpty())
			return;

		if (!Boolean.parseBoolean(ConfigIO.readConfig("state", "logstatus")))
			return;

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
		for (Map.Entry<CharacterNames, Pair<OnlineStatus, Activity>> statusEntry : statusMap.entrySet()) {
			// Number
			stringBuilder.append(counter);
			stringBuilder.append(". ");

			// Status
			stringBuilder.append("[STATUS]");

			// Character
			stringBuilder
				.append("[")
				.append(statusEntry.getKey())
				.append("]");

			// Online status
			stringBuilder
				.append("[")
				.append(statusEntry.getValue().getFirst())
				.append("]");

			// Activity
			stringBuilder
				.append(statusEntry.getValue().getSecond())
				.append(" | ");

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
}
