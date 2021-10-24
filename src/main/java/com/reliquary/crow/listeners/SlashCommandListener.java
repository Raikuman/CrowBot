package com.reliquary.crow.listeners;

import com.reliquary.crow.commands.manager.CommandManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class SlashCommandListener extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(TextChannelListener.class);
	private final CommandManager manager = new CommandManager();

	@Override
	public void onReady(@Nonnull ReadyEvent event) {
		logger.info("{} SlashListener is initialized", event.getJDA().getSelfUser().getAsTag());
	}

	@Override
	public void onSlashCommand(SlashCommandEvent event) {
		User user = event.getUser();

		// Check if author is bot, return
		if (user.isBot())
			return;

		// Check for the command name
		if (!event.getName().equals("dice")) return;
		event.reply("Dice").setEphemeral(false).queue();
	}
}
