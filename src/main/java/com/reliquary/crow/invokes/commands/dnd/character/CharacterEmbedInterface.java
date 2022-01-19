package com.reliquary.crow.invokes.commands.dnd.character;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.util.List;

/**
 * This class provides an interface for creating character embeds
 *
 * @version 1.1 2022-18-01
 * @since 1.1
 */
public interface CharacterEmbedInterface {

	/**
	 * This method returns the current user
	 * @return Returns user object
	 */
	User getUser();

	/**
	 * This method reloads the data in the current embed class
	 */
	void reloadInterface();

	/**
	 * This method provides a list of embeds found in the current embed class
	 * @return Returns a list of embeds
	 */
	List<EmbedBuilder> provideEmbeds();

	/**
	 * This method provides a list of single-cell ranges to get data from
	 * @return Returns a list of strings
	 */
	List<String> cellInfo();

	/**
	 * This method provides a list of range-cell ranges to get data from
	 *
	 * @return Returns a list of strings
	 */
	default List<String> cellRangeInfo() {
		return null;
	}
}
