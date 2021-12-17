package com.reliquary.crow.invokes.commands.music.Queue;

import com.reliquary.crow.invokes.commands.music.manager.GuildMusicManager;
import com.reliquary.crow.managers.commands.CommandContext;
import com.reliquary.crow.resources.jda.MessageResources;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.List;

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

		if (args.get(1) == null) {

			MessageResources.timedMessage(
				"You must provide a valid track number to delete",
				ctx.getChannel(),
				10
			);
		} else {

			int trackNum;

			// Check if arg is an integer
			try {
				trackNum = Integer.parseInt(args.get(1));
			} catch (NumberFormatException e) {
				MessageResources.timedMessage(
					"You must provide a valid track number to delete",
					ctx.getChannel(),
					10
				);
				return;
			}

			// Check if arg is greater than current queue
			if (trackNum > musicManager.scheduler.queue.size()) {
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
	}
}
