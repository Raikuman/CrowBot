package com.reliquary.crow.commands.manager;

import com.reliquary.crow.commands.Help;
import com.reliquary.crow.commands.admin.Shutdown;
import com.reliquary.crow.commands.basic.Ping;
import com.reliquary.crow.commands.dnd.Dice;
import com.reliquary.crow.commands.music.*;
import com.reliquary.crow.commands.music.Queue.Queue;
import com.reliquary.crow.commands.settings.ChangePrefix;
import com.reliquary.crow.resources.configs.ConfigHandler;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class provides the manager for all message commands from the guild. It will handle building the
 * command list while checking and handling the invocation of each command
 *
 * @version 2.0 2021-08-11
 * @since 1.0
 */
@SuppressWarnings("ConstantConditions")
public class CommandManager {

	private final List<CommandInterface> commands = new ArrayList<>();

	public CommandManager() {

		// Basic Commands
		addCommand(new Ping());

		// DnD Commands
		addCommand(new Dice());

		// Music Commands
		addCommand(new Join());
		addCommand(new Play());
		addCommand(new Stop());
		addCommand(new Skip());
		addCommand(new NowPlaying());
		addCommand(new Queue());
		addCommand(new Repeat());
		addCommand(new Leave());
		addCommand(new Pause());
		addCommand(new Resume());

		// Setting Commands
		addCommand(new ChangePrefix());

		// Admin Commands
		addCommand(new Shutdown());

		// Help Command
		addCommand(new Help(this));
	}

	/**
	 * This method handles checking and adding a command to the list from the constructor
	 * @param cmd Gets the command object for the list
	 */
	private void addCommand(CommandInterface cmd) {

		// Check if the command already exists in the list
		boolean commandFound = this.commands.stream().anyMatch(
			(found) -> found.getInvoke().equalsIgnoreCase(cmd.getInvoke())
		);

		if (commandFound)
			throw new IllegalArgumentException("A command with this name already exists");

		// Add the command to the list
		commands.add(cmd);
	}

	/**
	 * This method returns a list of all the commands under the command manager
	 * @return Returns a list of all commands
	 */
	public List<CommandInterface> getCommands() {
		return commands;
	}

	/**
	 * This method gets a command using the search string to look for the command in the list
	 * @param search The search string to find a command
	 * @return Returns a command based on the search string, or null
	 */
	@Nullable
	public CommandInterface getCommand(String search) {

		// Search for the command in the array, return if found
		for (CommandInterface cmd : this.commands)
			if (cmd.getInvoke().equalsIgnoreCase(search) || cmd.getAliases().contains(search))
				return cmd;

		// Return null, command not found
		return null;
	}

	/**
	 * This method handles breaking down the message received event in order to check if the message is a
	 * command, then builds the context of the event for command handling
	 * @param event The message received event to build the context for the command
	 */
	public void handle(GuildMessageReceivedEvent event) {

		// Remove the prefix from the message, split into a string array
		String[] split = event.getMessage().getContentRaw()
			.replaceFirst("(?i)" +
				Pattern.quote(ConfigHandler.loadConfigSetting("botSettings", "prefix")), "")
			.split("\\s+");

		// Find the command using the invoke string
		CommandInterface cmd = this.getCommand(split[0].toLowerCase());

		// Check if the command exists
		if (cmd != null) {

			// Remove the invoke string and create a list of args
			List<String> args = Arrays.asList(split).subList(1, split.length);

			// Create CommandContext and handle the command
			cmd.handle(new CommandContext(event, args));
		}
	}
}
