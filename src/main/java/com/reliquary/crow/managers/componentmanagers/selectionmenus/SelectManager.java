package com.reliquary.crow.managers.componentmanagers.selectionmenus;

import com.reliquary.crow.invokes.slashcommands.help.resources.HelpResources;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides the manager for all selections from the guild. It will handle building the selection
 * list while checking and handling the invocation of each selection
 *
 * @version 1.0 2021-07-12
 * @since 1.0
 */
public class SelectManager {

	private final List<SelectInterface> selects = new ArrayList<>();

	public SelectManager() {

		// Help selection
		addSelects(HelpResources.getSelectionMenuInterfaces());
	}

	/**
	 * This method handles checking and adding a select to the list
	 * @param select Gets the select object for the list
	 */
	private void addSelect(SelectInterface select) {

		// Check if the select already exists in the list
		boolean selectFound = this.selects.stream().anyMatch(
			(found) -> found.getMenuValue().equalsIgnoreCase(select.getMenuValue())
		);

		if (selectFound)
			throw new IllegalArgumentException("A select with this name already exists");

		// Add the select to the list
		selects.add(select);
	}

	/**
	 * This method uses the addSelect method to bulk-add a list of selects
	 * @param selects Gets a list of selections to add
	 */
	private void addSelects(List<SelectInterface> selects) {

		for (SelectInterface select : selects)
			addSelect(select);
	}

	/**
	 * This method returns a list of all selects under the select manager
	 * @return Returns a list of all selections
	 */
	public List<SelectInterface> getSelects() {
		return selects;
	}

	/**
	 * This method gets a selection using the search string to look for the select in the list
	 * @param search The search string to find the select
	 * @return Returns a select based on the search string, or null
	 */
	@Nullable
	public SelectInterface getSelect(String search) {

		// Search for the select in the array, return if found
		for (SelectInterface select : this.selects)
			if (select.getMenuValue().equalsIgnoreCase(search))
				return select;

		return null;
	}

	/**
	 * This method handles checking the select event and check if a select can be created using the event's id
	 * @param event The selection event to build the context for the select
	 * @param selectValue The id/value to create the select
	 */
	public void handle(SelectionMenuEvent event, String selectValue) {

		// Get select
		SelectInterface select = this.getSelect(selectValue);

		if (select != null) {
			// Build SelectContext
			SelectContext ctx = new SelectContext(event);

			select.handle(ctx);
		}
	}
}
