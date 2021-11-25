package com.reliquary.crow.listeners;

import com.reliquary.crow.reactions.manager.ReactInterface;
import com.reliquary.crow.reactions.manager.ReactManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

@SuppressWarnings("ConstantConditions")
public class ReactionEventListener extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ReactionEventListener.class);
	private final ReactManager manager = new ReactManager();

	@Override
	public void onReady(@Nonnull ReadyEvent event) {
		logger.info("{} ReactionListener is initialized", event.getJDA().getSelfUser().getAsTag());
	}

	@Override
	public void onGenericGuildMessageReaction(@NotNull GenericGuildMessageReactionEvent event) {

		User user = event.getUser();

		// Check if user is a bot, return
		if (user.isBot())
			return;

		// Find the react using the event's reaction emote
		ReactInterface react = manager.getReact(event.getReactionEmote().getAsCodepoints());

		if (react != null) {
			if (event instanceof GuildMessageReactionAddEvent)
				manager.handleAdd((GuildMessageReactionAddEvent) event);
			else if (event instanceof GuildMessageReactionRemoveEvent)
				manager.handleRemove((GuildMessageReactionRemoveEvent) event);
		}
	}
}
