package com.reliquary.crow.commands.music;

import com.reliquary.crow.commands.manager.CommandContext;
import com.reliquary.crow.commands.manager.CommandInterface;
import com.reliquary.crow.commands.music.manager.PlayerManager;
import com.reliquary.crow.resources.jda.MessageResources;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * This class handles automatically joining the user's voice channel on command and checking args to play
 * the found track on the music manager
 *
 * @version 2.0 2021-04-11
 * @since 1.0
 */
@SuppressWarnings("ConstantConditions")
public class Play implements CommandInterface {

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

		// Check if the bot is in another voice channel
		if (selfVoiceState.inVoiceChannel() &&(selfVoiceState.getChannel() != memberVoiceState.getChannel())) {
			MessageResources.timedMessage(
				"I'm already in a voice channel: `" + selfVoiceState.getChannel().getName() + "`",
				channel,
				10
			);
			return;
		}

		// Check if the bot has permissions to join user's voice channel
		if (!self.hasPermission(Permission.VOICE_CONNECT)) {
			MessageResources.timedMessage(
				"I don't have permission to join `" + memberVoiceState.getChannel().toString() + "`",
				channel,
				10
			);
			return;
		}

		// Check if args are empty
		if (ctx.getArgs().isEmpty()) {
			MessageResources.timedMessage(
				"You must enter a valid link or search for a video",
				channel,
				10
			);
			return;
		}

		// Join user's voice channel
		ctx.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());

		// Check if args is a link
		String link = String.join(" ", ctx.getArgs());

		// Add ytsearch if args is not a link
		if (!isUrl(link))
			link = "ytsearch:" + link;

		// Load player with link
		PlayerManager.getInstance().loadAndPlay(channel, link, ctx.getAuthor());

		// Delete original play command
		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "play";
	}

	@Override
	public String getHelp() {
		return "Play a song from a link or playlist";
	}

	@Override
	public String getUsage() {
		return "<link>";
	}

	@Override
	public String getCategory() {
		return "music";
	}

	/**
	 * This method checks a given string and returns a boolean of whether the string is an url or not
	 * @param url Takes in a string to check if it's an url
	 * @return Return boolean after string check
	 */
	private boolean isUrl(String url) {

		try {
			new URI(url);
			return true;
		} catch (URISyntaxException e) {
			return false;
		}
	}
}
