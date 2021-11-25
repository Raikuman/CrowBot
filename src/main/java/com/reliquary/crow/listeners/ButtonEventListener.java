package com.reliquary.crow.listeners;

import com.reliquary.crow.commands.music.Queue.QueueResources;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class ButtonEventListener extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ButtonEventListener.class);

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

		// Return if the author is not pressing the buttons and that the event is from a guild
		if (!authorId.equals(event.getUser().getId()) && event.isFromGuild())
			return;

		// Queue handler
		switch (type) {
			case "queueleft":
			case "queueright":
				QueueResources.queueHandler(event, type);
				break;
		}


	}
}
