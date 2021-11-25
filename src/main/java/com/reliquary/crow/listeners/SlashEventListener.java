package com.reliquary.crow.listeners;

import com.reliquary.crow.slashcommands.manager.SlashInterface;
import com.reliquary.crow.slashcommands.manager.SlashManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class SlashEventListener extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(SlashEventListener.class);
	private final SlashManager manager = new SlashManager();

	@Override
	public void onReady(@Nonnull ReadyEvent event) {
		logger.info("{} SlashCommandListener is initialized", event.getJDA().getSelfUser().getAsTag());

		slashCommandUpserter(event.getJDA());
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

	private void slashCommandUpserter(JDA jda) {

		// Seems like this is better to implement than upsert?
		// Takes up to an hour
		//CommandListUpdateAction slashCmd = event.getJDA().updateCommands();
		// slashCmd.addCommands( new CommandData());

		for (Guild guild : jda.getGuilds())
			for (SlashInterface slcmd : manager.getSlashCommands())
				guild.upsertCommand(new CommandData(slcmd.getInvoke(), slcmd.getHelp())).queue();
	}
}
