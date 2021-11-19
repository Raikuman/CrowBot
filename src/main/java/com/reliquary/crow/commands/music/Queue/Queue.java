package com.reliquary.crow.commands.music.Queue;

import com.reliquary.crow.commands.manager.CommandContext;
import com.reliquary.crow.commands.manager.CommandInterface;
import com.reliquary.crow.commands.music.manager.GuildMusicManager;
import com.reliquary.crow.commands.music.manager.PlayerManager;
import com.reliquary.crow.resources.other.RandomColor;
import com.reliquary.crow.resources.configs.ConfigHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.Button;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles all logic to manipulate the queue of the guild's music manager.
 *
 * There are two goals that queue achieves:
 * 1. Displaying a queue of the currently playing track and all queued tracks
 * 2. Clearing the queue using arguments from the command
 *
 * @version 1.0
 * @since 2021-04-11
 */
public class Queue implements CommandInterface {

	/**
	 * This is the main method that handles invoke for the Queue command and checks the args so that the
	 * command can be handled by sending an embed
	 * or clearing the queue.
	 * @param ctx Provides context for the command
	 */
	@Override
	public void handle(CommandContext ctx) {

		final TextChannel channel = ctx.getChannel();
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());

		// Check whether user is getting the queue embed or clearing the queue
		if (ctx.getArgs().isEmpty())
			queueCommand(channel, musicManager, ctx.getEvent().getAuthor().getId());
		else
			deleteQueueCommand(ctx, musicManager);
	}

	@Override
	public String getInvoke() {
		return "queue";
	}

	@Override
	public String getHelp() {
		return "Shows the queue of tracks";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getCategory() {
		return "music";
	}

	/**
	 * This method handles creating the embed for the queue and creates buttons for listening, extended
	 * into the QueueResources.java file
	 * @param channel Provides a text channel for sending the embed
	 * @param musicManager Provides the music manager for getting tracks
	 * @param userId Provides invoke author's id to identify embed buttons
	 */
	public void queueCommand(TextChannel channel, GuildMusicManager musicManager, String userId) {

		int numTracks = musicManager.scheduler.queue.size();

		// Calculate page number
		int numPages = (int) Math.ceil((numTracks + 1) / (double) QueueResources.NUM_TRACKS_PAGE);

		// Base Queue embed
		EmbedBuilder builder = new EmbedBuilder()
			.setAuthor("Page 1/" + numPages)
			.setColor(RandomColor.getRandomColor());
		StringBuilder descriptionBuilder = builder.getDescriptionBuilder();

		// Build description with current queue
		QueueResources.buildDescription(
			descriptionBuilder,
			new ArrayList<>(musicManager.scheduler.queue),
			musicManager.audioPlayer.getPlayingTrack().getInfo(),
			1
		);

		// Send embed with two buttons
		channel.sendMessageEmbeds(builder.build())
			.setActionRow(
				Button.secondary(userId + ":queueleft", Emoji.fromMarkdown("⬅️")),
				Button.secondary(userId + ":queueright", Emoji.fromMarkdown("➡️"))
			).queue();
	}

	/**
	 * This method checks the args from the context and, provided the correct args, will clear the music
	 * manager's queue
	 * @param ctx Provides context for the command
	 * @param musicManager Provides the music manager for getting queue
	 */
	public void deleteQueueCommand(CommandContext ctx, GuildMusicManager musicManager) {

		// Check for args
		List<String> args = ctx.getArgs();
		if (args.size() != 1)
			return;

		if (!args.get(0).equalsIgnoreCase("delete")) {
			ctx.getChannel().sendMessage(
					"Use `" +
						ConfigHandler.loadConfigSetting("botSettings", "prefix") +
						" remove` to clear the queue")
				.delay(Duration.ofSeconds(10))
				.flatMap(Message::delete)
				.queue();
			return;
		}

		if (musicManager.scheduler.queue.size() == 0) {
			ctx.getChannel().sendMessage(
					"There are currently no tracks in the queue")
				.delay(Duration.ofSeconds(10))
				.flatMap(Message::delete)
				.queue();
			return;
		}

		// Clear queue
		musicManager.scheduler.queue.clear();

		// Send queue clear reaction
		ctx.getEvent().getMessage()
			.addReaction("U+1F5D1").queue();
	}
}