package com.reliquary.crow.resources.jda;

import net.dv8tion.jda.api.entities.MessageReaction;

import java.util.List;

/**
 * This class handles all methods to aid with react-type classes
 *
 * @version 1.1 2021-25-12
 * @since 1.0
 */
public class ReactionResources {

	/**
	 * This method counts how many of a specific reaction there are in a given list and returns that amount
	 * @param reactionList The list of reactions of a message
	 * @param compareReaction The reaction to compare with the list
	 * @return Returns the number of reactions
	 */
	public static int countReactions(List<MessageReaction> reactionList, String compareReaction) {

		for (MessageReaction reaction : reactionList)
			if (reaction.getReactionEmote().getAsCodepoints().equalsIgnoreCase(compareReaction))
				return reaction.getCount();

		return 0;
	}
}
