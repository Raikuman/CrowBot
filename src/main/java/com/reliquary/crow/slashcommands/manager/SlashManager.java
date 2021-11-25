package com.reliquary.crow.slashcommands.manager;

import com.reliquary.crow.slashcommands.Help;
import com.reliquary.crow.slashcommands.dnd.Dice;
import com.reliquary.crow.slashcommands.dnd.IsDnD;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides the manager for all slash commands from the guild. It will handle building the slash
 * command list while checking and handling the invocation of each slash command
 *
 * @version 1.0
 * @since 1.0
 */
public class SlashManager {

	private final List<SlashInterface> slashCommands = new ArrayList<>();

	public SlashManager() {

		// DnD Commands
		addSlash(new Dice());
		addSlash(new IsDnD());
		addSlash(new Help());
	}

	/**
	 * This method handles checking and adding a slash command to the list from the constructor
	 * @param slcmd Gets the slash command object for the list
	 */
	private void addSlash(SlashInterface slcmd) {

		// Check if the command already exists in the array
		boolean nameFound = this.slashCommands.stream().anyMatch(
			(it) -> it.getInvoke().equalsIgnoreCase(slcmd.getInvoke())
		);

		if (nameFound)
			throw new IllegalArgumentException(
				"A slash command with this name already exists"
			);

		// Add the slash command to the array
		slashCommands.add(slcmd);
	}

	/**
	 * This method returns a list of all the slash commands under the command manager
	 * @return Returns a list of all slash commands
	 */
	public List<SlashInterface> getSlashCommands() {
		return slashCommands;
	}

	/**
	 * This method gets a slash command using the search string to look for the slash command in the list
	 * @param search The search string to find the slash command
	 * @return Returns a slash command based on the search string, or null
	 */
	@Nullable
	public SlashInterface getSlashCommand(String search) {

		// Ensure search string is in same case as array
		String searchLower = search.toLowerCase();

		// Search for command in array, return if found
		for (SlashInterface cmd : this.slashCommands)
			if (cmd.getInvoke().equals(searchLower))
				return cmd;

		// Return null, command not found
		return null;
	}

	/**
	 * This method handles checking the slash command event and check if a slash command can be created
	 * using the event's name
	 * @param event The slash command event to build the context for the slash command
	 */
	public void handle(SlashCommandEvent event) {

		// Get slash command
		SlashInterface cmd = this.getSlashCommand(event.getName());

		// Check if the slash command exists
		if (cmd != null) {
			// Build SlashContext
			SlashContext ctx = new SlashContext(event);

			// Send to handler
			cmd.handle(ctx);
		}
	}
}
