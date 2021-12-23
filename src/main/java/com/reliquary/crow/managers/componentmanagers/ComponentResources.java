package com.reliquary.crow.managers.componentmanagers;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides methods to make the workflow of components easier
 *
 * @version 1.0 2021-23-12
 * @since 1.1
 */
public class ComponentResources {

	/**
	 * This method creates action rows given a list of components
	 * @param components Provides the list of components
	 * @return Returns a list of action rows
	 */
	public static List<ActionRow> getActionRows(List<Component> components) {

		List<Component> componentRow = new ArrayList<>();
		List<ActionRow> actionRows = new ArrayList<>();

		int count = 1;
		for (Component component : components) {

			componentRow.add(component);

			if (count == 5) {
				actionRows.add(ActionRow.of(componentRow));
				componentRow = new ArrayList<>();
				count = 1;
			}

			count++;
		}

		if (!componentRow.isEmpty())
			actionRows.add(ActionRow.of(componentRow));

		return actionRows;
	}

}
