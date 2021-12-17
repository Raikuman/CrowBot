package com.reliquary.crow.invokes.commands.music.Queue;

import com.reliquary.crow.invokes.commands.music.manager.GuildMusicManager;
import com.reliquary.crow.invokes.commands.music.manager.PlayerManager;
import com.reliquary.crow.managers.commands.CommandContext;
import com.reliquary.crow.managers.commands.CommandInterface;
import com.reliquary.crow.resources.jda.MessageResources;
import com.reliquary.crow.resources.pagination.Pagination;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.List;

/**
 * This class handles all logic to manipulate the queue of the guild's music manager.
 *
 * There are two goals that queue achieves:
 * 1. Displaying a queue of the currently playing track and all queued tracks
 * 2. Clearing the queue using arguments from the command
 * 3. Deleting a track in the queue
 *
 * @version 3.1 2021-16-12
 * @since 1.0
 */
public class Queue implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {

		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());

		// Check args
		List<String> args = ctx.getArgs();

		if (args.isEmpty()) {

			// Send base queue embed
			ctx.getChannel().sendMessageEmbeds(
				Pagination.buildEmbedList(
					getInvoke(),
					QueuePagination.buildStrings(musicManager),
					10
				).get(0).build()
			).setActionRow(Pagination.provideButtons(getInvoke(), ctx.getMember().getId())).queue();
		} else if (args.get(0).contains("clear")) {

			// Clear the queue
			clearQueue(musicManager, ctx);
		} else if (args.get(0).contains("remove")) {

			// Remove a track
			removeTrack(args, musicManager, ctx);
		}

	}

	@Override
	public String getInvoke() {
		return "queue";
	}

	@Override
	public String getHelp() {
		return "Show the current queue of tracks";
	}

	@Override
	public String getUsage() {
		return "<clear>, <remove>";
	}

	@Override
	public String getCategory() {
		return "music";
	}

	/**
	 * This method clears the music manager queue
	 * @param musicManager Provides the music manager to check queue
	 * @param ctx Provides command context to reply to
	 */
	private void clearQueue(GuildMusicManager musicManager, CommandContext ctx) {

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
	private void removeTrack(List<String> args, GuildMusicManager musicManager, CommandContext ctx) {

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