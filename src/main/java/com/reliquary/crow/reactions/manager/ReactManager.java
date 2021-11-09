package com.reliquary.crow.reactions.manager;

import com.reliquary.crow.reactions.YamBoard;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides the manager for all reaction events from the guild. It handles building the react
 * list while checking and handling the invocation for reactions
 *
 * @version 1.0
 * @since 2021-09-11
 */
public class ReactManager {

	private final List<ReactInterface> reactions = new ArrayList<>();

	public ReactManager() {

		addReact(new YamBoard());
	}

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

	public List<ReactInterface> getReactions() {
		return reactions;
	}

	@Nullable
	public ReactInterface getReact(String search) {

		// Search for the react in the list, return if found
		for (ReactInterface react : this.reactions)
			if (react.getInvoke().equalsIgnoreCase(search))
				return react;

		// Return null, react not found
		return null;
	}

	public void handleAdd(GuildMessageReactionAddEvent event) {

		// Find the react using the event's reaction emote
		ReactInterface react = this.getReact(event.getReactionEmote().getAsCodepoints());

		// Check if the react exists
		if (react != null)
			react.handleAdd(new ReactContext(event));
	}

	public void handleRemove(GuildMessageReactionRemoveEvent event) {

		// Find the react using the event's reaction emote
		ReactInterface react = this.getReact(event.getReactionEmote().getAsCodepoints());

		// Check if the react exists
		if (react != null)
			react.handleRemove(new ReactContext(event));
	}
}
