package com.reliquary.crow.slashcommands.manager;

/**
 * This class provides an interface for creating slash commands
 *
 * @version 1.0
 * @since 1.0
 */
public interface SlashInterface {

	/**
	 * This method is where all functions of the slash command should go
	 * @param ctx Uses the context of a slash event for command usage
	 */
	void handle(SlashContext ctx);

	/**
	 * This method returns the invoke string from the slash command
	 * @return Returns the invocation string
	 */
	String getInvoke();

	/**
	 * This method returns the help description from the slash command
	 * @return Returns the help description string
	 */
	String getHelp();
}
