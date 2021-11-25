package com.reliquary.crow.commands.music;

import com.reliquary.crow.commands.manager.CommandContext;
import com.reliquary.crow.commands.manager.CommandInterface;
import com.reliquary.crow.commands.music.manager.GuildMusicManager;
import com.reliquary.crow.commands.music.manager.PlayerManager;
import com.reliquary.crow.resources.jda.MessageResources;
import com.reliquary.crow.resources.other.RandomColor;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * This class handles setting the music manager to repeat the current track
 *
 * @version 2.0 2021-04-11
 * @since 1.0
 */
@SuppressWarnings("ConstantConditions")
public class Repeat implements CommandInterface {

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

		// Handle repeat
		musicManager.scheduler.repeating = !musicManager.scheduler.repeating;

		// Handle embed title whether music is repeating or not
		String ifRepeat;
		if (musicManager.scheduler.repeating)
			ifRepeat = "\uD83D\uDD01 Now repeating track:";
		else
			ifRepeat = "\uD83D\uDEAB Stopped repeating track:";

		// Get current track
		AudioTrack track = musicManager.scheduler.player.getPlayingTrack();

		// Send info embed
		EmbedBuilder builder = new EmbedBuilder()
			.setAuthor(ifRepeat, track.getInfo().uri, ctx.getMember().getUser().getAvatarUrl())
			.setTitle(track.getInfo().title, track.getInfo().uri)
			.setColor(RandomColor.getRandomColor());

		// Send message
		channel.sendMessageEmbeds(builder.build()).queue();

		// Delete original repeat command
		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "repeat";
	}

	@Override
	public String getHelp() {
		return "Loops current track";
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
