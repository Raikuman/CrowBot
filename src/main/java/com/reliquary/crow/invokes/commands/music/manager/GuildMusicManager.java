package com.reliquary.crow.invokes.commands.music.manager;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

/**
 * This class creates an object using the audio handler and manager to create a single audio manager for
 * the guild
 *
 * @version 2.0 2021-04-11
 * @since 1.0
 */
public class GuildMusicManager {

	public final AudioPlayer audioPlayer;
	public final TrackScheduler scheduler;
	private final AudioPlayerSendHandler sendHandler;

	public GuildMusicManager(AudioPlayerManager manager) {
		this.audioPlayer = manager.createPlayer();
		this.scheduler = new TrackScheduler(this.audioPlayer);
		this.audioPlayer.addListener(this.scheduler);
		this.sendHandler = new AudioPlayerSendHandler(this.audioPlayer);
	}

	public AudioPlayerSendHandler getSendHandler() {
		return sendHandler;
	}
}
