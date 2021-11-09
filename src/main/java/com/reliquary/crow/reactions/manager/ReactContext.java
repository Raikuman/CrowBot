package com.reliquary.crow.reactions.manager;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;

/**
 * This class provides needed information for reacts in a single object without all the bloat from the
 * reaction event
 *
 * @version 1.0
 * @since 2021-09-11
 */
public class ReactContext {

	private final GenericGuildMessageReactionEvent event;

	public ReactContext(GenericGuildMessageReactionEvent event) {
		this.event = event;
	}

	/**
	 * This method returns the guild of where the event took place
	 * @return Returns the guild
	 */
	public Guild getGuild() {
		return this.getEvent().getGuild();
	}

	/**
	 * This method returns the event of the reaction
	 * @return Returns the event
	 */
	public GenericGuildMessageReactionEvent getEvent() {
		return event;
	}

	/**
	 * This method returns the text channel of where the event took place
	 * @return Returns the text channel of the event
	 */
	public TextChannel getChannel() {
		return event.getChannel();
	}

	/**
	 * This method returns the reaction emote of the event
	 * @return Returns the reaction emote of the event
	 */
	public MessageReaction.ReactionEmote getReactionEmote() {
		return event.getReactionEmote();
	}
}
