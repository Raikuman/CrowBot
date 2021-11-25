package com.reliquary.crow.commands.music;

import com.reliquary.crow.commands.manager.CommandContext;
import com.reliquary.crow.commands.manager.CommandInterface;
import com.reliquary.crow.resources.jda.MessageResources;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

/**
 * This class handles the bot joining a voice channel of a user
 *
 * @version 2.0 2021-04-11
 * @since 1.0
 */
@SuppressWarnings("ConstantConditions")
public class Join implements CommandInterface {

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

		// Join user's voice channel
		ctx.getGuild().getAudioManager().openAudioConnection(memberVoiceState.getChannel());

		// Send join reaction
		ctx.getEvent().getMessage()
			.addReaction("U+1F197").queue();
	}

	@Override
	public String getInvoke() {
		return "join";
	}

	@Override
	public String getHelp() {
		return "Lets the bot join your voice channel";
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
