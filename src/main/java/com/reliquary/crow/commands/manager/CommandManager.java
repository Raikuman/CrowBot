package com.reliquary.crow.commands.manager;

import com.reliquary.crow.commands.Help;
import com.reliquary.crow.commands.admin.Shutdown;
import com.reliquary.crow.commands.basic.Ping;
import com.reliquary.crow.commands.dnd.Dice;
import com.reliquary.crow.commands.settings.ChangePrefix;
import com.reliquary.crow.resources.configs.ConfigHandler;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {

	private final List<CommandInterface> commands = new ArrayList<>();
	private final ConfigHandler configHandler = new ConfigHandler();

	// Constructor to add commands
	public CommandManager() {

		// Basic Commands
		addCommand(new Ping());

		// DnD Commands
		addCommand(new Dice());

		// Setting Commands
		addCommand(new ChangePrefix());

		// Admin Commands
		addCommand(new Shutdown());

		// Help Command
		addCommand(new Help(this));
	}

	/*
	addCommand
	Adds a new command from a command interface into the array of valid commands
	 */
	private void addCommand(CommandInterface cmd) {

		// Check if the command already exists in the array
		boolean nameFound = this.commands.stream().anyMatch(
			(it) -> it.getInvoke().equalsIgnoreCase(cmd.getInvoke())
		);

		if (nameFound)
			throw new IllegalArgumentException(
				"A command with this name already exists"
			);

		// Add the command to the array
		commands.add(cmd);
	}

	/*
	getCommands
	Returns a list of all command interfaces
	 */
	public List<CommandInterface> getCommands() {
		return commands;
	}

	/*
	getCommand
	Return a single command interface
	 */
	@Nullable
	public CommandInterface getCommand(String search) {

		// Ensure search string is in same case as array
		String searchLower = search.toLowerCase();

		// Search for command in array, return if found
		for (CommandInterface cmd : this.commands)
			if (cmd.getInvoke().equals(searchLower) || cmd.getAliases().contains(searchLower))
				return cmd;

		// Return null, command not found
		return null;
	}

	/*
	handle
	Handles invocation of commands from message event
	 */
	public void handle(GuildMessageReceivedEvent event) {

		// Remove prefix, split input into string array
		String[] split = event.getMessage().getContentRaw()
			.replaceFirst("(?i)" +
				Pattern.quote(
					configHandler.loadConfigSetting("botSettings", "prefix")),
					""
				)
			.split("\\s+");

		// Get invoke string for command
		String invoke = split[0].toLowerCase();
		CommandInterface cmd = this.getCommand(invoke);

		// Check if invoke has a command
		if (cmd != null) {
			event.getChannel().sendTyping().queue();

			// Remove invoke string, add args to list
			List<String> args = Arrays.asList(split)
				.subList(1, split.length);

			// Build CommandContext
			CommandContext ctx = new CommandContext(event, args);

			// Send to handler
			cmd.handle(ctx);
		}
	}
}
