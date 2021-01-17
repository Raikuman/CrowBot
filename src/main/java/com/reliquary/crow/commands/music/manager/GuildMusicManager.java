package com.reliquary.crow.commands.music.manager;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class GuildMusicManager {

	public final AudioPlayer audioPlayer;
	public final TrackScheduler scheduler;
	private final AudioPlayerSendHandler sendHandler;

	// Constructor
	public GuildMusicManager(AudioPlayerManager manager) {
		this.audioPlayer = manager.createPlayer();
		this.scheduler = new TrackScheduler(this.audioPlayer);
		this.audioPlayer.addListener(this.scheduler);
		this.sendHandler = new AudioPlayerSendHandler(this.audioPlayer);
	}

	// Accessors
	public AudioPlayerSendHandler getSendHandler() {
		return sendHandler;
	}
}
