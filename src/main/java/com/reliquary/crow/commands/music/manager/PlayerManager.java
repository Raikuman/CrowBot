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

/**
 * This class provides methods to getting the music manager from the guild and handling playing tracks
 *
 * @version 1.0
 * @since 2021-04-11
 */
public class PlayerManager {

	private static PlayerManager INSTANCE;
	private final Map<Long, GuildMusicManager> musicManagers;
	private final AudioPlayerManager audioPlayerManager;

	public PlayerManager() {
		this.musicManagers = new HashMap<>();
		this.audioPlayerManager = new DefaultAudioPlayerManager();

		// Check where source of track is
		AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
		AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
	}

	/**
	 * This method gets the player manager from the guild and provides the general music manager for
	 * manipulation
	 * @param guild Provides the guild to get the audio manager
	 * @return Returns the music manager for the guild
	 */
	public GuildMusicManager getMusicManager(Guild guild) {

		return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
			final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);

			guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

			return guildMusicManager;
		});
	}

	/**
	 * This method overrides results from loading tracks onto the music manager to work with searching, url
	 * loading, and error handling
	 * @param channel Provides the guild text channel to send messages
	 * @param trackUrl Provides the argument of the play command, whether it is a url or a string
	 * @param user Provides the user object to get information in the embed
	 */
	public void loadAndPlay(TextChannel channel, String trackUrl, User user) {

		GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

		// Set at a constant 50% volume, as full volume was too loud when the bot hasn't been adjusted
		musicManager.audioPlayer.setVolume(50);

		// Override load result handler
		this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

			@Override
			public void trackLoaded(AudioTrack audioTrack) {

				// Appends audioTrack to the queue
				musicManager.scheduler.queue(audioTrack);

				// Send message
				channel.sendMessageEmbeds(
						trackEmbed(
							musicManager.scheduler.queue.size(),
							audioTrack,
							user).build())
					.queue();
			}

			@Override
			public void playlistLoaded(AudioPlaylist audioPlaylist) {

				List<AudioTrack> tracks = audioPlaylist.getTracks();

				// Check if the url is a searched track
				if (trackUrl.contains("ytsearch:")) {

					// Get the first track of the playlist
					AudioTrack firstTrack = audioPlaylist.getTracks().remove(0);

					if (firstTrack == null)
						return;

					// Queues the first track from the search playlist
					musicManager.scheduler.queue(firstTrack);

					// Send message
					channel.sendMessageEmbeds(
						trackEmbed(
							musicManager.scheduler.queue.size(),
							firstTrack,
							user).build())
						.queue();
					return;
				}

				// Queue a playlist if provided a link
				for (AudioTrack track : tracks) {
					musicManager.scheduler.queue(track);
				}

				// Playlist embed
				EmbedBuilder builder = new EmbedBuilder()
					.setAuthor("Adding playlist to queue:", null, user.getAvatarUrl())
					.setTitle(audioPlaylist.getName(), tracks.get(0).getInfo().uri)
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

	/**
	 * Method provides the player manager
	 * @return Returns the player manager instance
	 */
	public static PlayerManager getInstance() {

		// Assign INSTANCE when needed
		if (INSTANCE == null)
			INSTANCE = new PlayerManager();

		return INSTANCE;
	}

	/**
	 * This method creates an embed builder using information on a track and the user
	 * @param queueSize Gets the queue size of the current queue
	 * @param audioTrack Gets the current track being played
	 * @param user Get user information for the embed
	 * @return Return the embed builder after editing
	 */
	private EmbedBuilder trackEmbed(int queueSize, AudioTrack audioTrack, User user) {

		// Playing embed
		EmbedBuilder builder = new EmbedBuilder()

			.setTitle(audioTrack.getInfo().title, audioTrack.getInfo().uri)
			.setColor(RandomColor.getRandomColor());
		builder.addField("Channel", audioTrack.getInfo().author, true);
		builder.addField("Song Duration", DateAndTime.formatTime(audioTrack.getDuration()), true);


		// Set info based on queue size
		String title;
		String nowPlaying;
		if (queueSize == 0) {
			title = "▶️ Playing:";
			nowPlaying = "Now playing";
		}
		else {
			title = "⏭️ Adding to queue:";
			nowPlaying = String.valueOf(queueSize);
		}

		// Edit embed with given info
		builder
			.setAuthor(title, audioTrack.getInfo().uri, user.getAvatarUrl())
			.addField("Position in queue", nowPlaying, true);

		return builder;
	}
}
