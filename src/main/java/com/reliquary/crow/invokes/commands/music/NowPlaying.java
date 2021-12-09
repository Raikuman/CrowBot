package com.reliquary.crow.invokes.commands.music;

import com.reliquary.crow.managers.commands.CommandContext;
import com.reliquary.crow.managers.commands.CommandInterface;
import com.reliquary.crow.invokes.commands.music.manager.GuildMusicManager;
import com.reliquary.crow.invokes.commands.music.manager.PlayerManager;
import com.reliquary.crow.resources.jda.MessageResources;
import com.reliquary.crow.resources.other.DateAndTime;
import com.reliquary.crow.resources.other.RandomColor;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class handles showing an embed with the currently playing track
 *
 * @version 2.0 2021-04-11
 * @since 1.0
 */
@SuppressWarnings("ConstantConditions")
public class NowPlaying implements CommandInterface {

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

		// Get track
		AudioTrack track = audioPlayer.getPlayingTrack();

		// Send info embed
		EmbedBuilder builder = new EmbedBuilder()
			.setAuthor("♪ Now Playing ♪", track.getInfo().uri, ctx.getMember().getUser().getAvatarUrl())
			.setTitle(track.getInfo().title, track.getInfo().uri)
			.setColor(RandomColor.getRandomColor())
			.addField("Channel", track.getInfo().author, true)
			.addField("Song Duration",
				DateAndTime.formatTime(track.getPosition()) + "/" + DateAndTime.formatTime(track.getDuration()),
				true);

		// Send message
		channel.sendMessageEmbeds(builder.build())
			.queue();
	}

	@Override
	public String getInvoke() {
		return "nowplaying";
	}

	@Override
	public String getHelp() {
		return "Gets information about the current track";
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
			"np"
		));
	}
}
