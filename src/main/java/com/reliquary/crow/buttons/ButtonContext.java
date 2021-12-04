package com.reliquary.crow.buttons;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

/**
 * This class provides needed information for buttons in a single object without all the bloat from the
 * button event
 *
 * @version 1.0 2021-03-12
 * @since 1.0
 */
public class ButtonContext {

	private final ButtonClickEvent event;

	public ButtonContext(ButtonClickEvent event) {
		this.event = event;
	}

	/**
	 * This method returns the event of the button click
	 * @return Returns button click event
	 */
	public ButtonClickEvent getEvent() {
		return this.event;
	}

	/**
	 * This method returns the text channel of where the event took place
	 * @return Returns the text channel of the event
	 */
	public TextChannel getChannel() {
		return this.event.getTextChannel();
	}

	/**
	 * This method returns the guild of where the event took place
	 * @return Returns the guild
	 */
	public Guild getGuild() {
		return this.event.getGuild();
	}
}
