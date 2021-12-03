package com.reliquary.crow.buttons;

import net.dv8tion.jda.api.entities.Emoji;

/**
 * This class provides an interface for creating buttons
 *
 * @version 1.0 2021-03-12
 * @since 1.0
 */
public interface ButtonInterface {

	/**
	 * This method is where all functions of the button should go
	 * @param ctx Uses the context of a button event for usage
	 */
	void handle(ButtonContext ctx);

	/**
	 * This method returns the button id string from the button
	 * @return Returns the button id
	 */
	String getButtonId();

	/**
	 * This method returns the emoji that the button will use
	 * @return Returns the emoji
	 */
	Emoji getEmoji();

	/**
	 * This method returns the label that the button will show
	 * @return Returns label string
	 */
	String getLabel();
}
