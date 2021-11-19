package com.reliquary.crow.commands.music;

import com.reliquary.crow.commands.manager.CommandContext;
import com.reliquary.crow.commands.manager.CommandInterface;
import com.reliquary.crow.commands.music.manager.GuildMusicManager;
import com.reliquary.crow.commands.music.manager.PlayerManager;
import com.reliquary.crow.resources.jda.MessageResources;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class handles the bot leaving a voice channel and making sure the audio track queue is cleared
 *
 * @version 1.0
 * @since 2021-04-11
 */
@SuppressWarnings("ConstantConditions")
public class Leave implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {

		final TextChannel channel = ctx.getChannel();
		final Member self = ctx.getGuild().getSelfMember();
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

		// Leave voice channel
		ctx.getGuild().getAudioManager().closeAudioConnection();

		// Clear playing track and queue
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
		musicManager.scheduler.queue.clear();
		musicManager.scheduler.player.stopTrack();

		// Send leave reaction
		ctx.getEvent().getMessage()
			.addReaction("U+1F44B").queue();
	}

	@Override
	public String getInvoke() {
		return "leave";
	}

	@Override
	public String getHelp() {
		return "Leaves the voice channel";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getCategory() {
		return "music";
	}

	@Override
	public List<String> getAliases() {
		return new ArrayList<>(Collections.singletonList(
			"fuckoff"
		));
	}
}
