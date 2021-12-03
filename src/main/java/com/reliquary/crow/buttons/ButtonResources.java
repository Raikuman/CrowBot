package com.reliquary.crow.buttons;

import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a method for all classes that need to use buttons to create components to add to a
 * message
 *
 * @version 1.0 2021-03-12
 * @since 1.0
 */
public class ButtonResources {

	/**
	 * This method creates Button components with a given list of button interfaces
	 * @param userId Provides the userId to add to the component id
	 * @param buttons Provides the list of ButtonInterfaces to create Button components
	 * @return Returns a list of Button components
	 */
	public static List<Component> createButtons(String userId, List<ButtonInterface> buttons) {

		List<Component> components = new ArrayList<>();

		for (ButtonInterface btn : buttons)

			// Check if button is using either emoji or label
			if (btn.getEmoji() == null) {
				components.add(
					Button.secondary(userId + ":" + btn.getButtonId(), btn.getLabel())
				);
			} else {
				components.add(
					Button.secondary(userId + ":" + btn.getButtonId(), btn.getEmoji())
				);
			}

		return components;
	}
}
