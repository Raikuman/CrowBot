package com.reliquary.crow.commands.music.manager;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

	private static PlayerManager INSTANCE;
	private final Map<Long, GuildMusicManager> musicManagers;
	private final AudioPlayerManager audioPlayerManager;

	// Constructor
	public PlayerManager() {
		this.musicManagers = new HashMap<>();
		this.audioPlayerManager = new DefaultAudioPlayerManager();

		// Check where source of track is
		AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
		AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
	}

	/*
	getMusicManager
	Gets the music manager for the specific guild
	 */
	public GuildMusicManager getMusicManager(Guild guild) {

		return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
			final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);

			guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

			return guildMusicManager;
		});
	}

	/*
	loadAndPlay

	 */
	public void loadAndPlay(TextChannel channel, String trackUrl) {

		GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

		this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

			@Override
			public void trackLoaded(AudioTrack audioTrack) {

				// Appends audioTrack to the queue
				musicManager.scheduler.queue(audioTrack);

				// Playing embed
			}

			@Override
			public void playlistLoaded(AudioPlaylist audioPlaylist) {

			}

			@Override
			public void noMatches() {

			}

			@Override
			public void loadFailed(FriendlyException e) {

			}
		});
	}

	/*
	getInstance
	Provides instance of this class
	 */
	private static PlayerManager getInstance() {

		// Assign INSTANCE when needed
		if (INSTANCE == null)
			INSTANCE = new PlayerManager();

		return INSTANCE;
	}

}
