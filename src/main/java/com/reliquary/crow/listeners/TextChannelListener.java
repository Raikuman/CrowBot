package com.reliquary.crow.listeners;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class TextChannelListener extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {

		User user = event.getAuthor();

		// Check if author is bot
		if (user.isBot() || event.isWebhookMessage())
			return;

	}
}
