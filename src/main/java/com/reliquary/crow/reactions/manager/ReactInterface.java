package com.reliquary.crow.reactions.manager;

/**
 * This class provides an interface for creating commands
 *
 * @version 1.0
 * @since 2021-09-11
 */
public interface ReactInterface {

	/**
	 * This method is where all functions of the command of adding a reaction should go
	 * @param ctx Uses the context of the reaction for usage
	 */
	void handleAdd(ReactContext ctx);

	/**
	 * This method is where all functions of the command of removing a reaction should go
	 * @param ctx Uses the context of the reaction for usage
	 */
	void handleRemove(ReactContext ctx);

	/**
	 * This method returns the invoke reaction
	 * @return Returns the invocation reaction
	 */
	String getInvoke();
}
