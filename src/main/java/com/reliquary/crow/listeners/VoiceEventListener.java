package com.reliquary.crow.listeners;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class VoiceEventListener extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(VoiceEventListener.class);

	@Override
	public void onReady(@Nonnull ReadyEvent event) {
		logger.info("{} VoiceEventListener is initialized", event.getJDA().getSelfUser().getAsTag());
	}

	@Override
	public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {

		leaveOnEmpty(event);
	}

	private void leaveOnEmpty(GuildVoiceLeaveEvent event) {

		int numPeople = 0;
		for (Member member : event.getChannelLeft().getMembers()) {
			if (!member.getUser().isBot())
				numPeople++;
		}
		
		if (numPeople > 0)
			return;

		Member self = event.getGuild().getSelfMember();
		GuildVoiceState selfVoiceState = self.getVoiceState();

		// Check voice state
		if (selfVoiceState == null)
			return;

		// Check if bot is connected to a voice channel
		if (!selfVoiceState.inVoiceChannel()) {
			return;
		}

		VoiceChannel botChannel = selfVoiceState.getChannel();

		// Check that the bot is in the same channel
		if (event.getChannelLeft() != botChannel)
			return;

		// Close connection
		event.getGuild().getAudioManager().closeAudioConnection();


	}
}
