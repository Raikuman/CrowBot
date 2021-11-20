package com.reliquary.crow.listeners;

import com.reliquary.crow.slashcommands.manager.SlashManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class SlashCommandListener extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(SlashCommandListener.class);
	private final SlashManager manager = new SlashManager();

	@Override
	public void onReady(@Nonnull ReadyEvent event) {
		logger.info("{} SlashListener is initialized", event.getJDA().getSelfUser().getAsTag());

		// Create slash command on guild
		Guild guild = event.getJDA().getGuildById("685394913281441855");

		// Seems like this is better to implement than upsert?
			// Takes up to an hour
		//CommandListUpdateAction slashCmd = event.getJDA().updateCommands();
		// slashCmd.addCommands( new CommandData());

		assert guild != null;
		guild.upsertCommand(new CommandData("dice", "Rolls a d20"))
			.queue();
		guild.upsertCommand(new CommandData("isdnd", "a"))
			.queue();
		guild.upsertCommand(new CommandData("help", "Get some help!"))
			.queue();
	}

	@Override
	public void onSlashCommand(SlashCommandEvent event) {
		User user = event.getUser();

		// Check if author is bot, return
		if (user.isBot())
			return;

		// Check for the command name
		manager.handle(event);
	}
}
