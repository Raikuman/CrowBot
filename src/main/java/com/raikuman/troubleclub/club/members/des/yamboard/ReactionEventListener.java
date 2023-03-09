package com.raikuman.troubleclub.club.members.des.yamboard;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides an event listener for reactions, detecting if any users react to a message.
 *
 * @version 1.1 2023-07-03
 * @since 1.0
 */
public class ReactionEventListener extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ReactionEventListener.class);

	@Override
	public void onReady(@NotNull ReadyEvent event) {
		logger.info("{}" + ReactionEventListener.class.getName() + " is initialized",
			event.getJDA().getSelfUser().getAsTag());
	}

	@Override
	public void onGenericMessageReaction(@NotNull GenericMessageReactionEvent event) {
		Member member = event.getMember();

		if (member == null)
			return;

		if (member.getUser().isBot())
			return;

		YamboardManager.getInstance().handleReaction(event);
	}
}
