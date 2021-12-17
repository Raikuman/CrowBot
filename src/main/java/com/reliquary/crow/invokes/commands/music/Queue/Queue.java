package com.reliquary.crow.invokes.commands.music.Queue;

import com.reliquary.crow.invokes.commands.music.manager.GuildMusicManager;
import com.reliquary.crow.invokes.commands.music.manager.PlayerManager;
import com.reliquary.crow.managers.commands.CommandContext;
import com.reliquary.crow.managers.commands.CommandInterface;
import com.reliquary.crow.resources.pagination.Pagination;

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
			QueueArgs.clearQueue(musicManager, ctx);
		} else if (args.get(0).contains("remove")) {

			// Remove a track
			QueueArgs.removeTrack(args, musicManager, ctx);
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
}