package com.reliquary.crow.slashcommands.help.resources;

import com.reliquary.crow.commands.manager.CommandInterface;
import com.reliquary.crow.reactions.manager.ReactInterface;
import com.reliquary.crow.slashcommands.manager.SlashInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * This class gets a list of invokes based on a given category
 *
 * @version 1.0 2021-09-12
 * @since 1.0
 */
public class HelpCategoryProvider {

	final private HelpManagerProvider managerProvider;

	public HelpCategoryProvider() {
		managerProvider = new HelpManagerProvider();
	}

	/**
	 * This method gets the number of invokes from all managers of a given category
	 * @param category Provides the category string to check the managers with
	 * @return Returns the number of invokes
	 */
	public int getNumCategoryInvokes(String category) {

		return getCategoryCommands(category).size() +
			getCategorySlashes(category).size() +
			getCategoryReacts(category).size();
	}

	/**
	 * This method gets a list of CommandInterfaces of a given category
	 * @param category Provides the category string to check the commands with
	 * @return Returns the list of CommandInterfaces
	 */
	public List<CommandInterface> getCategoryCommands(String category) {

		List<CommandInterface> categoryCommands = new ArrayList<>();

		for (CommandInterface command : managerProvider.getCommandManager().getCommands()) {
			if (command.getCategory().equalsIgnoreCase(category))
				categoryCommands.add(command);
		}

		return categoryCommands;
	}

	/**
	 * This method gets a list of SlashInterfaces of a given category
	 * @param category Provides the category string to check the slashes with
	 * @return Returns the list of SlashInterfaces
	 */
	public List<SlashInterface> getCategorySlashes(String category) {

		List<SlashInterface> slashCommands = new ArrayList<>();

		for (SlashInterface slash : managerProvider.getSlashManager().getSlashCommands()) {
			if (slash.getCategory().equalsIgnoreCase(category))
				slashCommands.add(slash);
		}

		return slashCommands;
	}

	/**
	 * This method gets a list of ReactInterfaces of a given category
	 * @param category Provides the category string to check the reacts with
	 * @return Returns the list of ReactInterfaces
	 */
	public List<ReactInterface> getCategoryReacts(String category) {

		List<ReactInterface> reactCommands = new ArrayList<>();

		for (ReactInterface react : managerProvider.getReactManager().getReactions()) {
			if (react.getCategory().equalsIgnoreCase(category))
				reactCommands.add(react);
		}

		return reactCommands;
	}
}
