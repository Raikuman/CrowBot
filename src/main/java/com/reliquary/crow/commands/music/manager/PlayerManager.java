package com.reliquary.crow.commands.music.manager;

import com.reliquary.crow.resources.RandomClasses.DateAndTime;
import com.reliquary.crow.resources.RandomClasses.RandomColor;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
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
	public void loadAndPlay(TextChannel channel, String trackUrl, User user) {

		GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());
		musicManager.audioPlayer.setVolume(50);

		this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

			@Override
			public void trackLoaded(AudioTrack audioTrack) {

				// Appends audioTrack to the queue
				musicManager.scheduler.queue(audioTrack);

				// Send message
				channel.sendMessageEmbeds(trackEmbed(musicManager.scheduler.queue.size(), audioTrack, user).build())
					.queue();
			}

			@Override
			public void playlistLoaded(AudioPlaylist audioPlaylist) {

				AudioTrack firstTrack = audioPlaylist.getSelectedTrack();
				List<AudioTrack> tracks = audioPlaylist.getTracks();

				// Check for a searched track
				if (trackUrl.equalsIgnoreCase("ytsearch:")) {
					// Check if there is a track already playing
					if (firstTrack != null) {
						firstTrack = audioPlaylist.getTracks().remove(0);

						// Queues the first track from the search playlist
						musicManager.scheduler.queue(firstTrack);

						// Send message
						channel.sendMessageEmbeds(trackEmbed(musicManager.scheduler.queue.size(), firstTrack, user).build())
							.queue();

						return;
					}
				}

				// Queue a playlist if provided a link
				for (final AudioTrack track : tracks) {
					musicManager.scheduler.queue(track);
				}

				// Playlist embed
				EmbedBuilder builder = new EmbedBuilder()
					.setAuthor("**Adding playlist to queue** :notes:", null, user.getAvatarUrl())
					.setTitle(audioPlaylist.getName(), trackUrl)
					.setColor(RandomColor.getRandomColor());
				builder.addField("Songs in Playlist",
					"`" + audioPlaylist.getTracks().size() + "` songs",
					true);

				// Send message
				channel.sendMessageEmbeds(builder.build())
					.queue();
			}

			@Override
			public void noMatches() {
				channel.sendMessage("Nothing found using `" + trackUrl + "`")
					.delay(Duration.ofSeconds(10))
					.flatMap(Message::delete)
					.queue();
			}

			@Override
			public void loadFailed(FriendlyException e) {
				channel.sendMessage("Could not load track! `" + e.getMessage() + "`")
					.delay(Duration.ofSeconds(10))
					.flatMap(Message::delete)
					.queue();
			}
		});
	}

	/*
	getInstance
	Provides instance of this class
	 */
	public static PlayerManager getInstance() {

		// Assign INSTANCE when needed
		if (INSTANCE == null)
			INSTANCE = new PlayerManager();

		return INSTANCE;
	}

	/*
	trackEmbed
	Creates an EmbedBuilder for playing tracks
	 */
	private EmbedBuilder trackEmbed(int queueSize, AudioTrack audioTrack, User user) {

		// Set title based on queue size
		String title;
		if (queueSize == 1)
			title = "**Playing** :notes:";
		else
			title = "**Adding to queue** :notes:";

		// Playing embed
		EmbedBuilder builder = new EmbedBuilder()
			.setAuthor(title, audioTrack.getInfo().uri, user.getAvatarUrl())
			.setTitle(audioTrack.getInfo().title, audioTrack.getInfo().uri)
			.setColor(RandomColor.getRandomColor());
		builder.addField("Channel", audioTrack.getInfo().author, true);
		builder.addField("Song Duration", DateAndTime.formatTime(audioTrack.getDuration()), true);
		builder.addField("Position in queue", String.valueOf(audioTrack.getPosition()) + 1, true);

		return builder;
	}
}
