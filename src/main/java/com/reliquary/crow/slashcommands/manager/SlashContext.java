package com.reliquary.crow.slashcommands.manager;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class SlashContext {

	// Context manager for slash commands

	private final SlashCommandEvent event;

	public SlashContext(SlashCommandEvent event) {
		this.event = event;
	}

	public Guild getGuild() {
		return this.getEvent().getGuild();
	}

	public SlashCommandEvent getEvent() {
		return event;
	}

	public MessageChannel getChannel() {
		return event.getChannel();
	}
}
