package com.reliquary.crow.managers.componentmanagers.selectionmenus;

/**
 * This class provides an interface for creating select menu selections
 *
 * @version 1.0 2021-07-12
 * @since 1.0
 */
public interface SelectInterface {

	/**
	 * This method is where all functions of the selection should go
	 * @param ctx Uses the context of the selection event for command usage
	 */
	void handle (SelectContext ctx);

	/**
	 * This method returns the menu value (acts as an id) from the selection
	 * @return Returns the menu value id string
	 */
	String getMenuValue();

	/**
	 * This method returns the label of the selection
	 * @return Returns the selection label string
	 */
	String getLabel();

}
