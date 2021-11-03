package com.reliquary.crow.listeners;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ButtonClickListener extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(TextChannelListener.class);

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		logger.info("{} ButtonListener is initialized", event.getJDA().getSelfUser().getAsTag());




		// TODO: Complete button listener classes
	}
}
