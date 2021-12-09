package com.reliquary.crow.listeners;

import com.reliquary.crow.managers.commands.CommandManager;
import com.reliquary.crow.resources.configs.ConfigHandler;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class TextEventListener extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(TextEventListener.class);
	private final CommandManager manager = new CommandManager();

	@Override
	public void onReady(@Nonnull ReadyEvent event) {
		logger.info("{} TextListener is initialized", event.getJDA().getSelfUser().getAsTag());
	}

	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {

		User user = event.getAuthor();

		// Check if author is bot, return
		if (user.isBot() || event.isWebhookMessage())
			return;

		// Check for prefix, then handle event
		String prefix = ConfigHandler.loadConfigSetting(
			"botSettings",
			"prefix"
		);
		String raw = event.getMessage().getContentRaw();

		assert prefix != null;
		if (raw.startsWith(prefix))
			manager.handle(event);
	}
}
