package com.raikuman.troubleclub.club.statemanager.managers.reply;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides an event listener for replies
 *
 * @version 1.0 2023-22-02
 * @since 1.0
 */
public class ReplyEventListener extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ReplyEventListener.class);

	@Override
	public void onReady(@NotNull ReadyEvent event) {
		logger.info("{}" + ReplyEventListener.class.getName() + " is initialized",
			event.getJDA().getSelfUser().getAsTag());
	}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if (!event.isFromGuild())
			return;

		User user = event.getAuthor();

		if (user.isBot() || event.isWebhookMessage())
			return;

		ReplyStateManager.getInstance().handleEvent(event);
	}

}
