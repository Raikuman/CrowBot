package com.reliquary.crow.invokes.commands.music;

import com.reliquary.crow.managers.commands.CommandContext;
import com.reliquary.crow.managers.commands.CommandInterface;
import com.reliquary.crow.invokes.commands.music.manager.GuildMusicManager;
import com.reliquary.crow.invokes.commands.music.manager.PlayerManager;
import com.reliquary.crow.resources.jda.MessageResources;
import com.reliquary.crow.resources.other.RandomColor;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles skipping the currently playing track and sending an embed
 *
 * @version 2.0 2021-04-11
 * @since 1.0
 */
@SuppressWarnings("ConstantConditions")
public class Skip implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {

		final TextChannel channel = ctx.getChannel();
		final Member self = ctx.getSelfMember();
		final GuildVoiceState selfVoiceState = self.getVoiceState();

		// Check if the user is in a voice channel
		final GuildVoiceState memberVoiceState = ctx.getMember().getVoiceState();

		if (!memberVoiceState.inVoiceChannel()) {
			MessageResources.timedMessage(
				"You must be in a voice channel to use this command",
				channel,
				10
			);
			return;
		}

		// Check if the bot is in a voice channel
		if (!selfVoiceState.inVoiceChannel()) {
			MessageResources.timedMessage(
				"I must be in a voice channel to use this command",
				channel,
				10
			);
		}

		// Check if the bot is in another voice channel
		if (selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
			MessageResources.timedMessage(
				"You must be in the same voice channel to use this command: `" +
					selfVoiceState.getChannel().getName() + "`",
				channel,
				10
			);
			return;
		}

		// Get music manager
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
		final AudioPlayer audioPlayer = musicManager.audioPlayer;

		// Check if track is playing
		if (audioPlayer.getPlayingTrack() == null) {
			MessageResources.timedMessage(
				"There's currently no track playing",
				channel,
				10
			);
			return;
		}

		// Create Skip embed
		EmbedBuilder builder = new EmbedBuilder()
			.setColor(RandomColor.getRandomColor());
		String title;

		// Check if the queue is empty
		if (musicManager.scheduler.queue.isEmpty()) {
			title = "Skipped current track";
			builder
				.setAuthor(title, null, ctx.getMember().getUser().getAvatarUrl());
		} else {
			title = "Skipped current track, now playing: ";

			// Get current track
			List<AudioTrack> trackList = new ArrayList<>(musicManager.scheduler.queue);
			AudioTrack track = trackList.get(0);

			builder
				.setAuthor(title, track.getInfo().uri, ctx.getMember().getUser().getAvatarUrl())
				.setTitle(track.getInfo().title, track.getInfo().uri);
		}

		// Skip track
		musicManager.scheduler.nextTrack();

		// Send message
		channel.sendMessageEmbeds(builder.build())
			.queue();

		// Delete sent message
		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "skip";
	}

	@Override
	public String getHelp() {
		return "Skips the track that is currently playing";
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
