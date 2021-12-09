package com.reliquary.crow.componentmanagers.selectionmenus;

import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;

/**
 * This class provides the event of the selection menu to reply to
 *
 * @version 1.0 2021-07-12
 * @since 1.0
 */
public class SelectContext {

	private final SelectionMenuEvent event;

	public SelectContext(SelectionMenuEvent event) {
		this.event = event;
	}

	public SelectionMenuEvent getEvent() {
		return this.event;
	}
}
