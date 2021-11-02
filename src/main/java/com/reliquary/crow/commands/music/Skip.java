package com.reliquary.crow.commands.music;

import com.reliquary.crow.commands.manager.CommandContext;
import com.reliquary.crow.commands.manager.CommandInterface;
import com.reliquary.crow.commands.music.manager.GuildMusicManager;
import com.reliquary.crow.commands.music.manager.PlayerManager;
import com.reliquary.crow.resources.RandomClasses.RandomColor;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.Duration;

@SuppressWarnings("ConstantConditions")
public class Skip implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {

		final TextChannel channel = ctx.getChannel();
		final Member self = ctx.getSelfMember();
		final GuildVoiceState selfVoiceState = self.getVoiceState();

		// Check if a user is in a voice channel
		final Member member = ctx.getMember();
		final GuildVoiceState memberVoiceState = member.getVoiceState();

		if (!memberVoiceState.inVoiceChannel()) {
			channel.sendMessage("You must be in a voice channel to use this command")
				.delay(Duration.ofSeconds(10))
				.flatMap(Message::delete)
				.queue();
			return;
		}

		// Check if the bot is in a voice channel
		if (!selfVoiceState.inVoiceChannel()) {
			channel.sendMessage("I must be in a voice channel to use this command")
				.delay(Duration.ofSeconds(10))
				.flatMap(Message::delete)
				.queue();
			return;
		}

		// Check if the bot has permission to join the voice channel
		if (!self.hasPermission(Permission.VOICE_CONNECT)) {
			channel.sendMessage("I don't have permission to join `" + memberVoiceState.getChannel().toString() + "`")
				.delay(Duration.ofSeconds(10))
				.flatMap(Message::delete)
				.queue();
			return;
		}

		// Get music manager
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
		final AudioPlayer audioPlayer = musicManager.audioPlayer;

		// Check if track is playing
		if (audioPlayer.getPlayingTrack() == null) {
			channel.sendMessage("There's currently no track playing")
				.delay(Duration.ofSeconds(10))
				.flatMap(Message::delete)
				.queue();
			return;
		}

		// Skip track
		musicManager.scheduler.nextTrack();

		// Get current track
		AudioTrack track = musicManager.scheduler.player.getPlayingTrack();

		// Send info embed
		EmbedBuilder builder = new EmbedBuilder()
			.setAuthor("Skipped current track, now playing:", track.getInfo().uri, member.getUser().getAvatarUrl())
			.setTitle(track.getInfo().title, track.getInfo().uri)
			.setColor(RandomColor.getRandomColor());

		// Send message
		channel.sendMessageEmbeds(builder.build())
			.queue();
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
