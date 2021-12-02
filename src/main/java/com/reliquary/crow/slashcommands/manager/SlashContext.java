package com.reliquary.crow.slashcommands.manager;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

/**
 * This class provides needed information for slash commands in a single object without all the bloat from
 * the slash event
 *
 * @version 1.0 2021-25-11
 * @since 1.0
 */
public class SlashContext {

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

	public TextChannel getChannel() {
		return event.getTextChannel();
	}
}
