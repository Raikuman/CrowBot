package com.reliquary.crow.commands.music;

import com.reliquary.crow.commands.manager.CommandContext;
import com.reliquary.crow.commands.manager.CommandInterface;
import com.reliquary.crow.commands.music.manager.GuildMusicManager;
import com.reliquary.crow.commands.music.manager.PlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Queue implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {

		final TextChannel testChannel = ctx.getChannel();
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
		final BlockingQueue<AudioTrack> queue = musicManager.scheduler.queue;

		// Check if queue is empty
		if (queue.isEmpty()) {
			// Current queue is empty
			return;
		}

		// Get queue information
		final int trackCount = Math.min(queue.size(), 10);
		final List<AudioTrack> trackList = new ArrayList<>(queue);


		// Send queue embed
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
}
