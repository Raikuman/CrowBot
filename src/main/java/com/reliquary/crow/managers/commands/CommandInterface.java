package com.reliquary.crow.managers.commands;

import java.util.List;

/**
 * This class provides an interface for creating commands
 *
 * @version 2.0 2021-08-11
 * @since 1.0
 */
public interface CommandInterface {

	/**
	 * This method is where all functions of the command should go
	 * @param ctx Uses the context of a message for command usage
	 */
	void handle(CommandContext ctx);

	/**
	 * This method returns the invoke string from the command
	 * @return Returns the invocation string
	 */
	String getInvoke();

	/**
	 * This method returns the help description from the command
	 * @return Returns the help description string
	 */
	String getHelp();

	/**
	 * This method returns the usage string, showing whether the command can contain extra arguments or not
	 * @return Returns the usage string
	 */
	String getUsage();

	/**
	 * This method returns the category of the command
	 * @return Returns the category string
	 */
	String getCategory();

	/**
	 * This method returns the list of aliases the command might have
	 * @return Returns a list of strings of aliases
	 */
	default List<String> getAliases() {
		return List.of();
	}
}
