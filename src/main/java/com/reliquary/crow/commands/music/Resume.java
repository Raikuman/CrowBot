package com.reliquary.crow.commands.music;

import com.reliquary.crow.commands.manager.CommandContext;
import com.reliquary.crow.commands.manager.CommandInterface;
import com.reliquary.crow.commands.music.manager.GuildMusicManager;
import com.reliquary.crow.commands.music.manager.PlayerManager;
import com.reliquary.crow.resources.jda.MessageResources;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * This class handles resuming the paused track on the music manager
 *
 * @version 2.0 2021-04-11
 * @since 1.0
 */
@SuppressWarnings("ConstantConditions")
public class Resume implements CommandInterface {

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

		// Check if track is paused
		if (!audioPlayer.isPaused()) {
			MessageResources.timedMessage(
				"A track is already playing",
				channel,
				10
			);
			return;
		}

		// Resume track
		audioPlayer.setPaused(false);

		// Send resume reaction
		ctx.getEvent().getMessage()
			.addReaction("U+25B6").queue();

	}

	@Override
	public String getInvoke() {
		return "resume";
	}

	@Override
	public String getHelp() {
		return "Resumes the current track";
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
