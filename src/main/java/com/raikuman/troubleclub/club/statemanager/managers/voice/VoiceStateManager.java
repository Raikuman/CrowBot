package com.raikuman.troubleclub.club.statemanager.managers.voice;

import com.raikuman.botutilities.configs.ConfigIO;
import com.raikuman.troubleclub.club.statemanager.managers.status.StatusStateManager;
import com.raikuman.troubleclub.club.utilities.CharacterNames;
import com.raikuman.troubleclub.club.statemanager.CharacterStateManager;
import com.raikuman.troubleclub.club.statemanager.CharacterStateObject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.security.SecureRandom;
import java.time.LocalTime;
import java.util.*;

/**
 * Manages the voice state for bot characters
 *
 * @version 1.0 2023-22-02
 * @since 1.0
 */
public class VoiceStateManager {

	private static VoiceStateManager instance = null;
	private VoiceChannel voiceChannel;

	public static VoiceStateManager getInstance() {
		if (instance == null)
			instance = new VoiceStateManager();

		return instance;
	}

	public VoiceChannel getVoiceChannel() {
		JDA jda = CharacterStateManager.getInstance().getCharacterStateMap().get(CharacterNames.CROW).getJda();
		if (voiceChannel == null)
			voiceChannel = jda.getVoiceChannelById(ConfigIO.readConfig("troubleclub/voice", "voicechannelid"));

		return voiceChannel;
	}

	/**
	 * Returns a hashmap of the current voice channel connection for each character bot
	 * @return The connection hashmap
	 */
	public HashMap<CharacterNames, Boolean> getConnectionMap() {
		HashMap<CharacterNames, Boolean> characterVoiceMap = new LinkedHashMap<>();
		for (Map.Entry<CharacterNames, CharacterStateObject> characterState :
			CharacterStateManager.getInstance().getCharacterStateMap().entrySet()) {
			characterVoiceMap.put(
				characterState.getKey(),
				characterState.getValue().getGuild().getAudioManager().isConnected()
			);
		}

		return characterVoiceMap;
	}

	/**
	 * Main method to update the voice channel connections for character bots
	 */
	public void updateConnectionsState() {
		HashMap<CharacterNames, Boolean> originalVoiceMap = getConnectionMap();
		HashMap<CharacterNames, Boolean> newVoiceMap = new LinkedHashMap<>();

		// Handle disconnecting all characters at off-hours
		Object hourChance = getHourMap().get(LocalTime.now().getHour());
		if (hourChance == null) {
			for (Map.Entry<CharacterNames, Boolean> originalEntry : originalVoiceMap.entrySet())
				newVoiceMap.put(originalEntry.getKey(), false);

			updateConnections(newVoiceMap);
			return;
		}

		// Handle updating new voice map
		SecureRandom rand = new SecureRandom();
		for (CharacterNames characterName : CharacterNames.values()) {
			// Check character status for handling connections
			OnlineStatus characterStatus = StatusStateManager.getInstance().getStatusMap().get(characterName);

			// Handle multiplier for connection
			int chanceMultiplier = 4;   // Disconnect multi
			if (!originalVoiceMap.get(characterName))
				chanceMultiplier = 5;   // Connect multi

			double chance = Math.abs((double) hourChance * chanceMultiplier - 1.0) / 2;
			// Check whether a connection should be handled
			if (rand.nextDouble() <= chance)
				continue;

			// Add character to new voice map
			newVoiceMap.put(
				characterName,
				!originalVoiceMap.get(characterName)
			);

			// Update status if applicable for connection
			CharacterStateObject characterState = CharacterStateManager.getInstance()
				.getCharacterStateMap().get(characterName);
			if (characterStatus == OnlineStatus.IDLE) {
				// Set presence to ONLINE
				characterState.getJda().getPresence().setPresence(
					OnlineStatus.ONLINE,
					characterState.getJda().getPresence().getActivity()
				);
			}
		}

		// Handle scarce voice channel
		if (handleScarceVoiceChannel(newVoiceMap))
			return;

		updateConnections(newVoiceMap);
	}

	/**
	 * Update the voice channel connections of character bots given a connection map of characters to update
	 * @param connectionMap The map to update the voice connections of the character bots
	 */
	public void updateConnections(HashMap<CharacterNames, Boolean> connectionMap) {
		List<CharacterNames> disconnectCharacters = new ArrayList<>();
		for (Map.Entry<CharacterNames, Boolean> connectionEntry : connectionMap.entrySet()) {
			AudioManager characterAudio = CharacterStateManager.getInstance().getCharacterStateMap()
				.get(connectionEntry.getKey()).getGuild().getAudioManager();

			if (connectionEntry.getValue()) {
				characterAudio.openAudioConnection(getVoiceChannel());
			} else {
				characterAudio.setSelfMuted(false);
				characterAudio.closeAudioConnection();
				disconnectCharacters.add(connectionEntry.getKey());
			}
		}

		logVoiceConnections(connectionMap, false);

		StatusStateManager.getInstance().handleGroupStatusDisconnect(disconnectCharacters);
	}

	/**
	 * Handles disconnecting all bots when a given connection map results in leaving the voice channel with
	 * a single character
	 * @param connectionMap The map to update the voice connections of the character bots
	 * @return True if the input connection map results in one character left over, false otherwise
	 */
	public boolean handleScarceVoiceChannel(HashMap<CharacterNames, Boolean> connectionMap) {
		int numConnected = 0;
		for (Map.Entry<CharacterNames, Boolean> originalConnectionEntry: getConnectionMap().entrySet()) {
			AudioManager characterAudio = CharacterStateManager.getInstance()
				.getCharacterStateMap().get(originalConnectionEntry.getKey()).getGuild().getAudioManager();

			// Skip characters not affected by connections
			if (connectionMap.get(originalConnectionEntry.getKey()) == null) {
				// Check if connected
				if (characterAudio.isConnected())
					numConnected++;

				continue;
			}

			if (connectionMap.get(originalConnectionEntry.getKey()))
				numConnected++;
			else
				numConnected--;
		}

		// Handle scarce channel disconnections
		if (numConnected < 2) {
			List<CharacterNames> disconnectCharacters = new ArrayList<>();
			for (CharacterNames characterName : CharacterNames.values()) {
				AudioManager characterAudio = CharacterStateManager.getInstance()
					.getCharacterStateMap().get(characterName).getGuild().getAudioManager();

				if (characterAudio.isConnected()) {
					characterAudio.setSelfMuted(false);
					characterAudio.closeAudioConnection();
					disconnectCharacters.add(characterName);
				}
			}

			// Log scarce channel
			logVoiceConnections(connectionMap, true);

			StatusStateManager.getInstance().handleGroupStatusDisconnect(disconnectCharacters);

			return true;
		}

		return false;
	}

	/**
	 * Log voice updates
	 * @param connectionMap The connection map to log
	 * @param scarceCheck Whether voice map is a result of scarcity checks or not
	 */
	private void logVoiceConnections(HashMap<CharacterNames, Boolean> connectionMap, boolean scarceCheck) {
		if (connectionMap.isEmpty())
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
		for (Map.Entry<CharacterNames, Boolean> originalConnectionEntry : getConnectionMap().entrySet()) {
			if (connectionMap.get(originalConnectionEntry.getKey()) == null)
				if (!scarceCheck)
					continue;

			// Number
			stringBuilder.append(counter);
			stringBuilder.append(". ");

			// Voice
			stringBuilder.append("[VOICE]");

			// Character
			stringBuilder
				.append("[")
				.append(originalConnectionEntry.getKey())
				.append("]");

			// Connection type
			if (connectionMap.get(originalConnectionEntry.getKey()) == null) {
				AudioManager characterAudio = CharacterStateManager.getInstance().getCharacterStateMap()
						.get(originalConnectionEntry.getKey()).getGuild().getAudioManager();

				if (characterAudio.isConnected())
					stringBuilder.append("[DISCONNECT][SCARCE]");
				else
					stringBuilder.append("[ATTEMPTED]");
			} else if (connectionMap.get(originalConnectionEntry.getKey())) {
				stringBuilder.append("[CONNECT]");
				if (scarceCheck)
					stringBuilder.append("[BYPASSED]");
			} else {
				stringBuilder.append("[DISCONNECT]");
			}

			stringBuilder.append(" ");

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
	 * A map of hours with weighted values
	 * @return The map of weighted hours
	 */
	private static HashMap<Integer, Double> getHourMap() {
		return new HashMap<>()
		{{
			put(0, 0.05);
			put(1, 0.04);
			put(2, 0.02);
			put(8, 0.01);
			put(9, 0.02);
			put(10, 0.02);
			put(11, 0.03);
			put(12, 0.04);
			put(13, 0.03);
			put(14, 0.02);
			put(15, 0.06);
			put(16, 0.07);
			put(17, 0.08);
			put(18, 0.09);
			put(19, 0.1);
			put(20, 0.08);
			put(21, 0.07);
			put(22, 0.1);
			put(23, 0.07);
		}};
	}
}
