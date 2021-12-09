package com.reliquary.crow.componentmanagers.selectionmenus;

import net.dv8tion.jda.api.interactions.components.selections.SelectOption;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a method for all classes that need to use select menus to create options to add to a
 * message
 *
 * @version 1.0 2021-09-12
 * @since 1.0
 */
public class SelectResources {

	/**
	 * This method creates SelectOption components with a given list of select interfaces
	 * @param userId Provides the userId to add to the component value/id
	 * @param selections Provides the list of SelectInterfaces to create SelectOption components
	 * @return Returns a list of SelectOptions
	 */
	public static List<SelectOption> createSelectOptions(String userId, List<SelectInterface> selections) {

		List<SelectOption> options = new ArrayList<>();

		for (SelectInterface selection : selections)
			options.add(SelectOption.of(selection.getLabel(), userId + ":" + selection.getMenuValue()));

		return options;
	}
}
