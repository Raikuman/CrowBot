package com.reliquary.crow.buttons;

import com.reliquary.crow.slashcommands.help.buttons.HelpCategories;
import com.reliquary.crow.slashcommands.help.buttons.HelpFun;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class provides the manager for all buttons from the guild. It will handle building the button list
 * while checking and handling the button event
 *
 * @version 1.0 2021-03-12
 * @since 1.0
 */
public class ButtonManager {

	private final List<ButtonInterface> buttons = new ArrayList<>();

	public ButtonManager() {
		addButtons(Arrays.asList(
			new HelpCategories(),
			new HelpFun()
		));
	}

	/**
	 * This method handles checking and adding a list of buttons
	 * @param btn Gets the button to add to the list
	 */
	private void addButton(ButtonInterface btn) {

		// Check if the button already exists in the list
		boolean buttonFound = this.buttons.stream().anyMatch(
			(found) -> found.getButtonId().equalsIgnoreCase(btn.getButtonId())
		);

		if (buttonFound)
			throw new IllegalArgumentException("A button with this name already exists");

		// Add the button to the list
		buttons.add(btn);
	}

	/**
	 * This method uses the addButton method to bulk-add a list of buttons
	 * @param btns Gets a list of buttons to add
	 */
	private void addButtons(List<ButtonInterface> btns) {

		for (ButtonInterface btn : btns)
			addButton(btn);
	}

	/**
	 * This method returns a list of all the buttons under the button manager
	 * @return Returns a list of all buttons
	 */
	public List<ButtonInterface> getButtons() {
		return buttons;
	}

	/**
	 * This method gets a button using the search string to look for the button in the list
	 * @param search The search string to find the button
	 * @return Returns a button based on the search string, or null
	 */
	@Nullable
	public ButtonInterface getButtonCommand(String search) {

		// Search for the button in the array, return if found
		for (ButtonInterface btn : this.buttons)
			if (btn.getButtonId().equalsIgnoreCase(search))
				return btn;

		return null;
	}

	/**
	 * This method handles checking the button event and check if a button can be created using the event's
	 * component id
	 * @param event The button event to build the context for the button
	 */
	public void handle(ButtonClickEvent event) {

		// Get button command
		ButtonInterface btn = this.getButtonCommand(event.getComponentId().split(":")[1]);

		if (btn != null) {
			// Build ButtonContext
			ButtonContext ctx = new ButtonContext(event);

			// Send to handler
			btn.handle(ctx);
		}
	}
}
