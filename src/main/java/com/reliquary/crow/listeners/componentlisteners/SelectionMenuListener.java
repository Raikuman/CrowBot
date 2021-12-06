package com.reliquary.crow.listeners.componentlisteners;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class SelectionMenuListener extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(SelectionMenuListener.class);

	@Override
	public void onReady(@Nonnull ReadyEvent event) {
		logger.info("{} SelectionMenuListener is initialized", event.getJDA().getSelfUser().getAsTag());
	}

	@Override
	public void onSelectionMenu(SelectionMenuEvent event) {

	}


}
