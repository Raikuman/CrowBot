package com.reliquary.crow.componentmanagers.buttons;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.UpdateInteractionAction;

import java.util.List;

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

	public UpdateInteractionAction getUpdateInteraction() {
		return this.event.deferEdit();
	}

	public List<Button> getButtons() {
		return this.event.getMessage().getButtons();
	}
}
