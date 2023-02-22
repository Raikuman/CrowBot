package com.raikuman.troubleclub.club.statemanager.managers.voice;

import com.raikuman.botutilities.configs.ConfigIO;
import com.raikuman.troubleclub.club.statemanager.CharacterStateManager;
import com.raikuman.troubleclub.club.statemanager.managers.status.StatusStateManager;
import com.raikuman.troubleclub.club.utilities.CharacterNames;
import kotlin.Pair;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.managers.Presence;

import java.security.SecureRandom;
import java.time.LocalTime;
import java.util.*;

/**
 * Manages idling the status of bots and handle muting with idling in the voice channel
 *
 * @version 1.0 2023-22-02
 * @since 1.0
 */
public class IdleStateManager {

	private static IdleStateManager instance = null;

	public static IdleStateManager getInstance() {
		if (instance == null)
			instance = new IdleStateManager();

		return instance;
	}

	/**
	 * Main method to update the idling and muting of character bots
	 */
	public void updateIdlingState() {
		HashMap<CharacterNames, OnlineStatus> originalStatusMap = StatusStateManager.getInstance().getStatusMap();
		HashMap<CharacterNames, OnlineStatus> newStatusMap = new LinkedHashMap<>();

		// Handle updating new status map
		SecureRandom rand = new SecureRandom();
		for (CharacterNames characterName : CharacterNames.values()) {
			if (rand.nextBoolean())
				continue;

			OnlineStatus originalStatus = originalStatusMap.get(characterName);
			// Don't update idling for characters outside of idling status
			if (!(originalStatus == OnlineStatus.IDLE || originalStatus == OnlineStatus.ONLINE))
				continue;

			OnlineStatus newStatus;
			if (originalStatus == OnlineStatus.IDLE)
				newStatus = OnlineStatus.ONLINE;
			else
				newStatus = OnlineStatus.IDLE;

			newStatusMap.put(
				characterName,
				newStatus
			);
		}

		updateIdling(newStatusMap);
		updateMuting(newStatusMap);
	}

	/**
	 * Update the idling of character bots given a status map of characters to update
	 * @param statusMap The map to update the idling of the character bots
	 */
	private void updateIdling(HashMap<CharacterNames, OnlineStatus> statusMap) {
		for (Map.Entry<CharacterNames, OnlineStatus> statusEntry : statusMap.entrySet()) {
			Presence characterPresence = CharacterStateManager.getInstance().getCharacterStateMap()
				.get(statusEntry.getKey()).getJda().getPresence();

			characterPresence.setPresence(
				statusEntry.getValue(),
				characterPresence.getActivity()
			);
		}

		logIdleChanges(statusMap);
	}

	/**
	 * Update the muting of character bots given an idle map of characters to mute
	 * @param idleMap The map to update the muting of the character bots
	 */
	private void updateMuting(HashMap<CharacterNames, OnlineStatus> idleMap) {
		if (idleMap.isEmpty())
			return;

		int numMuted = 0;
		for (Member member : VoiceStateManager.getInstance().getVoiceChannel().getMembers()) {
			GuildVoiceState memberVoiceState = member.getVoiceState();
			if (memberVoiceState == null)
				continue;

			if (memberVoiceState.isSelfMuted())
				numMuted++;
		}

		// Create a muting map from the given idle map
		HashMap<CharacterNames, Pair<OnlineStatus, Boolean>> muteMap = new LinkedHashMap<>();
		for (Map.Entry<CharacterNames, OnlineStatus> idleEntry : idleMap.entrySet()) {

			boolean mute =
				idleEntry.getValue() == OnlineStatus.IDLE || idleEntry.getValue() == OnlineStatus.DO_NOT_DISTURB;

			muteMap.put(
				idleEntry.getKey(),
				new Pair<>(
					idleEntry.getValue(),
					mute
				)
			);

			if (mute)
				numMuted++;
		}

		// Add characters in the voice channel and have the do not disturb status
		SecureRandom rand = new SecureRandom();
		for (CharacterNames characterName : CharacterNames.values()) {
			AudioManager characterAudio = CharacterStateManager.getInstance()
				.getCharacterStateMap().get(characterName).getGuild().getAudioManager();

			if (characterAudio.isConnected() &&
				StatusStateManager.getInstance().getStatusMap().get(characterName) == OnlineStatus.DO_NOT_DISTURB) {

				if (rand.nextBoolean())
					continue;

				muteMap.put(
					characterName,
					new Pair<>(OnlineStatus.DO_NOT_DISTURB, true)
				);

				numMuted++;
			}
		}

		// Remove characters not in voice channel
		List<CharacterNames> removeFromMuting = new ArrayList<>();
		for (Map.Entry<CharacterNames, OnlineStatus> idleEntry : idleMap.entrySet()) {
			AudioManager characterAudio = CharacterStateManager.getInstance()
				.getCharacterStateMap().get(idleEntry.getKey()).getGuild().getAudioManager();

			if (!characterAudio.isConnected())
				removeFromMuting.add(idleEntry.getKey());
		}

		for (CharacterNames characterName : removeFromMuting)
			muteMap.remove(characterName);

		int numConnected = VoiceStateManager.getInstance().getVoiceChannel().getMembers().size();

		if (muteMap.isEmpty())
			return;

		// Fix mute map so that voice channel has at least 2 characters unmuted
		List<Map.Entry<CharacterNames, Pair<OnlineStatus, Boolean>>> muteList = new ArrayList<>(muteMap.entrySet());
		while ((numConnected - numMuted < 2)) {
			Map.Entry<CharacterNames, Pair<OnlineStatus, Boolean>> muteEntry =
				muteList.get(rand.nextInt(muteList.size()));

			if (!muteEntry.getValue().getSecond())
				continue;

			OnlineStatus newOnlineStatus = muteEntry.getValue().getFirst();
			if (newOnlineStatus == OnlineStatus.IDLE)
				newOnlineStatus = OnlineStatus.ONLINE;

			muteMap.put(
				muteEntry.getKey(),
				new Pair<>(newOnlineStatus, false)
			);

			numMuted--;
		}

		// Handle muting
		for (Map.Entry<CharacterNames, Pair<OnlineStatus, Boolean>> muteEntry : muteMap.entrySet()) {
			AudioManager characterAudio = CharacterStateManager.getInstance()
				.getCharacterStateMap().get(muteEntry.getKey()).getGuild().getAudioManager();

			characterAudio.setSelfMuted(muteEntry.getValue().getSecond());
		}

		logMutingChanges(muteMap);
	}

	/**
	 * Log idle updates
	 * @param statusMap The status map to log
	 */
	private void logIdleChanges(HashMap<CharacterNames, OnlineStatus> statusMap) {
		if (statusMap.isEmpty())
			return;

		if (!Boolean.parseBoolean(ConfigIO.readConfig("state", "logidle")))
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
		for (Map.Entry<CharacterNames, OnlineStatus> statusEntry : statusMap.entrySet()) {
			// Number
			stringBuilder.append(counter);
			stringBuilder.append(". ");

			// Idling
			stringBuilder.append("[IDLING]");

			// Character
			stringBuilder
				.append("[")
				.append(statusEntry.getKey())
				.append("]");

			// Online status
			stringBuilder
				.append("[")
				.append(statusEntry.getValue())
				.append("]");

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
	 * Log muting updates
	 * @param muteMap The mute map to log
	 */
	private void logMutingChanges(HashMap<CharacterNames, Pair<OnlineStatus, Boolean>> muteMap) {
		if (muteMap.isEmpty())
			return;

		if (!Boolean.parseBoolean(ConfigIO.readConfig("state", "logmute")))
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
		for (Map.Entry<CharacterNames, Pair<OnlineStatus, Boolean>> muteEntry : muteMap.entrySet()) {
			// Number
			stringBuilder.append(counter);
			stringBuilder.append(". ");

			// Muting
			stringBuilder.append("[MUTING]");

			// Character
			stringBuilder
				.append("[")
				.append(muteEntry.getKey())
				.append("]");

			// Online status
			stringBuilder
				.append("[")
				.append(muteEntry.getValue().getFirst())
				.append("]");

			// Muting state

			if (muteEntry.getValue().getSecond())
				stringBuilder.append("[MUTED]");
			else
				stringBuilder.append("[UNMUTED]");

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
