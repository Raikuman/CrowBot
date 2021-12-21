package com.reliquary.crow.invokes.commands.music.Queue;

import com.reliquary.crow.invokes.commands.music.manager.GuildMusicManager;
import com.reliquary.crow.managers.commands.CommandContext;
import com.reliquary.crow.resources.jda.MessageResources;
import com.reliquary.crow.resources.other.DateAndTime;
import com.reliquary.crow.resources.other.RandomColor;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the logic for the arguments of the queue command
 *
 * @version 1.2 2021-16-12
 * @since 1.0
 */
public class QueueArgs {

	/**
	 * This method clears the music manager queue
	 * @param musicManager Provides the music manager to check queue
	 * @param ctx Provides command context to reply to
	 */
	protected static void clearQueue(GuildMusicManager musicManager, CommandContext ctx) {

		if (musicManager.scheduler.queue.size() == 0) {

			MessageResources.timedMessage(
				"There are currently no tracks in the queue",
				ctx.getChannel(),
				10
			);
		} else {

			musicManager.scheduler.queue.clear();
			ctx.getEvent().getMessage()
				.addReaction("U+1F5D1").queue();
		}
	}

	/**
	 * This method removes a track given the argument
	 * @param args Provides the arguments to check what track to remove
	 * @param musicManager Provides the music manager to get queue
	 * @param ctx Provides command context to reply to
	 */
	protected static void removeTrack(List<String> args, GuildMusicManager musicManager, CommandContext ctx) {

		int trackNum = getTrackFromArgs(args, ctx.getChannel(), musicManager.scheduler.queue.size());

		if (trackNum == -1) {
			MessageResources.timedMessage(
				"You must provide a valid track number to delete",
				ctx.getChannel(),
				10
			);
			return;
		}

		// Find and remove track
		int count = 1;
		for (AudioTrack audioTrack : musicManager.scheduler.queue) {

			if (trackNum == count) {
				if (musicManager.scheduler.queue.remove(audioTrack)) {
					ctx.getEvent().getMessage()
						.addReaction("U+1F5D1").queue();
				}

				break;
			}

			count++;
		}
	}

	/**
	 * This method jumps to a track in the queue and plays it
	 * @param args Provides the arguments to check what track to jump to
	 * @param musicManager Provides the music manager to get queue
	 * @param ctx Provides command context to reply to
	 */
	protected static void jumpToTrack(List<String> args, GuildMusicManager musicManager, CommandContext ctx) {

		int trackNum = getTrackFromArgs(args, ctx.getChannel(), musicManager.scheduler.queue.size());

		if (trackNum == -1) {
			MessageResources.timedMessage(
				"You must provide a valid track number to delete",
				ctx.getChannel(),
				10
			);
			return;
		}

		List<AudioTrack> audioTracks = new ArrayList<>();
		AudioTrack playTrack = null;
		musicManager.scheduler.queue.drainTo(audioTracks);

		// Get track from trackNum and remove track from list
		int count = 1;
		for (AudioTrack audioTrack : audioTracks) {

			if (trackNum == count) {
				playTrack = audioTrack;
				audioTracks.remove(audioTrack);
				break;
			}

			count++;
		}

		if (playTrack == null) {
			MessageResources.timedMessage(
				"Could not jump to track",
				ctx.getChannel(),
				10
			);
			return;
		}

		// Set the track to the beginning of the list
		audioTracks.set(0, playTrack);

		musicManager.scheduler.queue.addAll(audioTracks);
		musicManager.scheduler.nextTrack();

		ctx.getEvent().getMessage()
			.addReaction("U+23ED").queue();
	}

	/**
	 * This method repeats the current queue by adding the ending track to the end of the queue
	 * @param musicManager Provides the music manager
	 * @param ctx Provides command context to reply to
	 */
	protected static void repeatQueue(GuildMusicManager musicManager, CommandContext ctx) {

		// Negate repeat boolean
		musicManager.scheduler.repeatingQueue = !musicManager.scheduler.repeatingQueue;

		// Get queue length
		long queueLength = 0;
		for (AudioTrack audioTrack : musicManager.scheduler.queue) {
			queueLength += audioTrack.getDuration();
		}
		queueLength += musicManager.audioPlayer.getPlayingTrack().getDuration();

		EmbedBuilder builder = new EmbedBuilder().setColor(RandomColor.getRandomColor());

		// Handle embed title and field whether queue is repeating or not
		String ifRepeat;
		if (musicManager.scheduler.repeatingQueue) {
			ifRepeat = "\uD83D\uDD03 Now repeating queue";
			builder.addField("Queue Duration", DateAndTime.formatTime(queueLength), false);
		} else {
			ifRepeat = "\uD83D\uDEAB Stopped repeating queue";
		}

		builder.setAuthor(ifRepeat, null, ctx.getMember().getUser().getAvatarUrl());

		ctx.getChannel().sendMessageEmbeds(builder.build()).queue();
	}

	/**
	 * This method gets the track number from the args while providing error checks and messages
	 * @param args Provides args to get track number from
	 * @param channel Provides channel to send error messages to
	 * @param queueSize Provides queue size to check args against
	 * @return Returns the track number, -1 if not found
	 */
	private static int getTrackFromArgs(List<String> args, TextChannel channel, int queueSize) {

		// Check if there is an arg
		if (args.get(1) == null) {
			MessageResources.timedMessage(
				"You must provide a valid track number to delete",
				channel,
				10
			);
			return -1;
		}

		// Check if arg is an integer
		int trackNum;
		try {
			trackNum = Integer.parseInt(args.get(1));
		} catch (NumberFormatException e) {
			MessageResources.timedMessage(
				"You must provide a valid track number to delete",
				channel,
				10
			);
			return -1;
		}

		// Check if arg is greater than current queue
		if (trackNum > queueSize) {
			MessageResources.timedMessage(
				"You must provide a valid track number to delete",
				channel,
				10
			);
			return -1;
		}

		return trackNum;
	}
}
