package com.raikuman.troubleclub.club.voice;

import com.raikuman.botutilities.configs.ConfigIO;
import com.raikuman.troubleclub.club.main.JDAFinder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.security.SecureRandom;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Manages voice connections for bot characters
 *
 * @version 1.0 2023-23-01
 * @since 1.0
 */
public class VoiceManager {

	private static VoiceManager instance = null;
	private final List<VoiceObject> voiceObjectList = new ArrayList<>();
	private int numConnected = 0;

	public VoiceManager() {
		for (String character : JDAFinder.characterNames()) {
			// Get guild
			String guildConfig = ConfigIO.readConfig("voice", "guildid");
			if (guildConfig == null)
				return;

			Guild guild = JDAFinder.getInstance().getJDA(character).getSecond().getGuildById(guildConfig);
			if (guild == null)
				return;

			// Get voice channel
			String voiceConfig = ConfigIO.readConfig("voice", "voicechannelid");
			if (voiceConfig == null)
				return;

			VoiceChannel voiceChannel = guild.getVoiceChannelById(voiceConfig);
			if (voiceChannel == null)
				return;

			// Check voice state
			GuildVoiceState voiceState = guild.getSelfMember().getVoiceState();
			if (voiceState == null)
				return;

			voiceObjectList.add(new VoiceObject(
				guild,
				voiceChannel,
				voiceState,
				JDAFinder.getInstance().getJDA(character).getSecond()
			));
		}
	}

	public static VoiceManager getInstance() {
		if (instance == null)
			instance = new VoiceManager();

		return instance;
	}

	/**
	 * Handle character connections for all JDAs
	 */
	public void handleCharacterVoice() {
		for (VoiceObject voiceObject : voiceObjectList)
			handleVoiceConnection(voiceObject);

		handleVoiceChannelState();
	}

	/**
	 * Handles the voice connection to the voice channel, checking to randomly connect/disconnect from channel
	 * @param voiceObject The voice object used to handle connection/disconnection
	 */
	private void handleVoiceConnection(VoiceObject voiceObject) {
		SecureRandom rand = new SecureRandom();

		voiceObject.connected = voiceObject.voiceState.inAudioChannel();
		boolean handleConnection = false;

		// Check if voice connection should be handled
		Object chance = getHourMap().get(LocalTime.now().getHour());
		if (chance == null) {
			// Ensure connections are closed at null times
			for (VoiceObject voiceObject1 : voiceObjectList) {
				if (voiceObject1.connected) {
					voiceObject1.connected = false;
					voiceObject1.guild.getAudioManager().closeAudioConnection();
					numConnected--;
				}
			}
			return;
		}

		int handleVal = 6;
		if (!voiceObject.connected)
			handleVal = 4;

		double randNum = rand.nextDouble();
		double check = Math.abs((double) chance * handleVal - 1.0) / 2;
		if (randNum > check)
			handleConnection = true;

		if (handleConnection)
			if (voiceObject.connected) {
				voiceObject.connected = false;
				voiceObject.guild.getAudioManager().closeAudioConnection();
				numConnected--;
			} else {
				voiceObject.connected = true;
				voiceObject.guild.getAudioManager().openAudioConnection(voiceObject.voiceChannel);
				numConnected++;
			}
	}

	/**
	 * Handle when the voice channel only has 1 connected, then disconnect
	 */
	private void handleVoiceChannelState() {
		if (numConnected != 1)
			return;

		for (VoiceObject voiceObject : voiceObjectList) {
			if (voiceObject.connected) {
				voiceObject.connected = false;
				voiceObject.guild.getAudioManager().closeAudioConnection();
				numConnected--;
				break;
			}
		}
	}

	/**
	 * A map of hours with weighted values
	 * @return The map of hours
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

/**
 * An object to hold information on how to handle voice connection
 *
 * @version 1.0 2023-23-01
 * @since 1.0
 */
class VoiceObject {

	boolean connected;
	public Guild guild;
	public VoiceChannel voiceChannel;
	public GuildVoiceState voiceState;
	public JDA jda;

	public VoiceObject(Guild guild, VoiceChannel voiceChannel, GuildVoiceState voiceState, JDA jda) {
		this.guild = guild;
		this.voiceChannel = voiceChannel;
		this.voiceState = voiceState;
		this.jda = jda;
	}
}
