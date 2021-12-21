package com.reliquary.crow.listeners.componentlisteners;

import com.reliquary.crow.managers.componentmanagers.buttons.ButtonManager;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class ButtonEventListener extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ButtonEventListener.class);
	private final ButtonManager manager = new ButtonManager();

	@Override
	public void onReady(@Nonnull ReadyEvent event) {
		logger.info("{} ButtonListener is initialized", event.getJDA().getSelfUser().getAsTag());
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {

		// Split component for checking
		String[] id = event.getComponentId().split(":");
		String authorId = id[0];
		String type = id[1];

		// Return if the event isn't from a guild
		if (!event.isFromGuild())
			return;

		// Return if the author is not pressing the buttons
		if (!authorId.equals(event.getUser().getId())) {
			event.deferEdit().queue();
			return;
		}

		manager.handle(event, type);
	}
}
