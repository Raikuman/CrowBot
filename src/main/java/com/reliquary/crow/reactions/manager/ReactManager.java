package com.reliquary.crow.reactions.manager;

import com.reliquary.crow.reactions.YamBoard.YamBoard;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides the manager for all reaction events from the guild. It handles building the react
 * list while checking and handling the invocation for reactions
 *
 * @version 1.0.1
 * @since 2021-14-11
 */
public class ReactManager {

	private final List<ReactInterface> reactions = new ArrayList<>();

	public ReactManager() {

		addReact(new YamBoard());
	}

	/**
	 * This method handles checking and adding a react to the list from the constructor
	 * @param react Gets the react object for the list
	 */
	private void addReact(ReactInterface react) {

		// Check if the react already exists in the list
		boolean reactFound = this.reactions.stream().anyMatch(
			(found) -> found.getInvoke().equalsIgnoreCase(react.getInvoke())
		);

		if (reactFound)
			throw new IllegalArgumentException("A react with this name already exists");

		// Add the react to the list
		reactions.add(react);
	}

	/**
	 * This method returns a list of all the reacts under the react manager
	 * @return Returns a list of reacts
	 */
	public List<ReactInterface> getReactions() {
		return reactions;
	}

	/**
	 * This method gets a react using the search string to look for the react in the list
	 * @param search The search string to find a react
	 * @return Returns a react based on the search string, or null
	 */
	@Nullable
	public ReactInterface getReact(String search) {

		// Search for the react in the list, return if found
		for (ReactInterface react : this.reactions)
			if (react.getInvoke().equalsIgnoreCase(search))
				return react;

		// Return null, react not found
		return null;
	}

	/**
	 * This method handles getting the add reaction from the event context and checking if the emote has a
	 * react attached to it
	 * @param event The react add event to build the context for the add react
	 */
	public void handleAdd(GuildMessageReactionAddEvent event) {

		// Find the react using the event's reaction emote
		ReactInterface react = this.getReact(event.getReactionEmote().getAsCodepoints());

		// Check if the react exists
		if (react != null)
			react.handleAdd(new ReactContext(event));
	}

	/**
	 * This method handles getting the remove reaction from the event context and checking if the emote has a
	 * react attached to it
	 * @param event The react add event to build the context for the remove react
	 */
	public void handleRemove(GuildMessageReactionRemoveEvent event) {

		// Find the react using the event's reaction emote
		ReactInterface react = this.getReact(event.getReactionEmote().getAsCodepoints());

		// Check if the react exists
		if (react != null)
			react.handleRemove(new ReactContext(event));
	}
}
