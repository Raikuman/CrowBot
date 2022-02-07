package com.reliquary.crow.listeners;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
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
	public void onGuildVoiceUpdate(@Nonnull GuildVoiceUpdateEvent event) {

		Member self = event.getGuild().getSelfMember();
		GuildVoiceState selfVoiceState = self.getVoiceState();

		if (selfVoiceState == null)
			return;

		if (!selfVoiceState.inVoiceChannel())
			return;

		if (event.getChannelLeft() != selfVoiceState.getChannel())
			return;

		if (event.getChannelLeft() == null)
			return;

		int numPeople = 0;
		for (Member member : event.getChannelLeft().getMembers()) {
			if (!member.getUser().isBot())
				numPeople++;
		}
		
		if (numPeople > 0)
			return;

		// Close connection
		event.getGuild().getAudioManager().closeAudioConnection();
	}
}
