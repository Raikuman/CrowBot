package com.reliquary.crow.managers.componentmanagers.buttons;

import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a method for all classes that need to use buttons to create components to add to a
 * message
 *
 * @version 1.4 2021-23-12
 * @since 1.0
 */
public class ButtonResources {

	/**
	 * This method creates Button components with a given list of button interfaces
	 * @param userId Provides the userId to add to the component id
	 * @param buttons Provides the list of ButtonInterfaces to create Button components
	 * @param disableFirst Provides boolean whether to disable the first button
	 * @return Returns a list of Button components
	 */
	public static List<Component> createButtons(String userId, List<ButtonInterface> buttons,
		boolean disableFirst) {

		List<Component> components = new ArrayList<>();

		Button button;

		int count = 0;

		for (ButtonInterface btn : buttons) {

			// Check if button is using either emoji or label
			if (btn.getEmoji() == null)
				button = Button.secondary(userId + ":" + btn.getButtonId(), btn.getLabel());
			else
				button = Button.secondary(userId + ":" + btn.getButtonId(), btn.getEmoji());

			if (disableFirst && (count == 0)) {
				button = button.asDisabled();
				count++;
			}

			components.add(button);
		}

		return components;
	}

	/**
	 * This method modifies the list of buttons to set only the button with the id as disabled
	 * @param buttons Provides buttons from a message
	 * @param buttonId Provides the buttonId to disable
	 * @return Returns a list of buttons with one disabled
	 */
	public static List<Button> setCurrentButtonDisabled(List<Button> buttons, String buttonId) {

		int count = 0;
		for (Button button : buttons) {
			if (button.isDisabled())
				buttons.set(count, button.asEnabled());

			if (button.getId() != null)
				if (button.getId().split(":")[1].equalsIgnoreCase(buttonId))
					buttons.set(count, button.asDisabled());

			count++;
		}

		return buttons;
	}
}
