package com.reliquary.crow.slashcommands.help.resources;

import com.reliquary.crow.commands.manager.CommandInterface;
import com.reliquary.crow.reactions.manager.ReactInterface;
import com.reliquary.crow.resources.configs.ConfigHandler;
import com.reliquary.crow.slashcommands.manager.SlashInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * This class creates a list of strings for the help embed from the given category
 *
 * @version 1.0 2021-09-12
 * @since 1.0
 */
public class HelpStringBuilder {

	/**
	 * This method gets a list of strings of a given category
	 * @param category Provides the category string to get a list of invokes
	 * @return Returns a list of strings of invokes
	 */
	public List<String> getCategoryStrings(String category) {

		List<String> categoryStrings = new ArrayList<>();

		categoryStrings.addAll(buildSlashStrings(category));
		categoryStrings.addAll(buildCommandStrings(category));
		categoryStrings.addAll(buildReactStrings(category));

		return categoryStrings;
	}

	/**
	 * This method gets a list of strings of commands
	 * @param category Provides the category string to get a list of commands
	 * @return Returns a list of strings of commands
	 */
	private List<String> buildCommandStrings(String category) {

		List<String> commandStrings = new ArrayList<>();
		String commandString;

		for (CommandInterface command : new HelpCategoryProvider().getCategoryCommands(category)) {
			commandString = ConfigHandler.loadConfigSetting("botSettings", "prefix") +
				command.getInvoke();

			if (!command.getUsage().isEmpty())
				commandString += " " + command.getUsage();

			commandString += " :: " + command.getHelp();

			commandStrings.add(commandString);
		}

		return commandStrings;
	}

	/**
	 * This method gets a list of strings of slashes
	 * @param category Provides the category string to get a list of slashes
	 * @return Returns a list of strings of slashes
	 */
	private List<String> buildSlashStrings(String category) {

		List<String> slashStrings = new ArrayList<>();
		String slashString;

		for (SlashInterface slash : new HelpCategoryProvider().getCategorySlashes(category)) {
			slashString = "/" + slash.getInvoke() + " :: " + slash.getHelp();

			slashStrings.add(slashString);
		}

		return slashStrings;
	}

	/**
	 * This method gets a list of strings of reacts
	 * @param category Provides the category string to get a list of reacts
	 * @return Returns a list of strings of reacts
	 */
	private List<String> buildReactStrings(String category) {

		List<String> reactStrings = new ArrayList<>();
		String reactString;

		for (ReactInterface react : new HelpCategoryProvider().getCategoryReacts(category)) {
			reactString = react.getEmoji() + " " + react.getReactName() + " :: " + react.getHelp();

			reactStrings.add(reactString);
		}

		return reactStrings;
	}
}
