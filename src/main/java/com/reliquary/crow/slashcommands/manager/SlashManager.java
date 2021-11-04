package com.reliquary.crow.slashcommands.manager;

import com.reliquary.crow.slashcommands.dnd.Dice;
import com.reliquary.crow.slashcommands.dnd.IsDnD;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SlashManager {

	private final List<SlashInterface> slashcommands = new ArrayList<>();

	// Constructor to add slash commands
	public SlashManager() {
		// DnD Commands
		addCommand(new Dice());
		addCommand(new IsDnD());
	}

	/*
	addCommand
	Adds a new slash command from a slash command interface into the array of valid commands
	 */
	private void addCommand(SlashInterface slcmd) {

		// Check if the command already exists in the array
		boolean nameFound = this.slashcommands.stream().anyMatch(
			(it) -> it.getInvoke().equalsIgnoreCase(slcmd.getInvoke())
		);

		if (nameFound)
			throw new IllegalArgumentException(
				"A slash command with this name already exists"
			);

		// Add the slash command to the array
		slashcommands.add(slcmd);
	}

	/*
	getCommands
	Returns a list of all slash command interfaces
	 */
	public List<SlashInterface> getCommands() {
		return slashcommands;
	}

	/*
	getCommand
	Return a single slash command interface
	 */
	@Nullable
	public SlashInterface getCommand(String search) {

		// Ensure search string is in same case as array
		String searchLower = search.toLowerCase();

		// Search for command in array, return if found
		for (SlashInterface cmd : this.slashcommands)
			if (cmd.getInvoke().equals(searchLower) || cmd.getAliases().contains(searchLower))
				return cmd;

		// Return null, command not found
		return null;
	}

	/*
	handle
	Handles invocation of slash commands from message event
	 */
	public void handle(SlashCommandEvent event) {

		// Get slash command
		SlashInterface cmd = this.getCommand(event.getName());

		// Check if the slash command exists
		if (cmd != null) {
			// Build SlashContext
			SlashContext ctx = new SlashContext(event);

			// Send to handler
			cmd.handle(ctx);
		}
	}
}
