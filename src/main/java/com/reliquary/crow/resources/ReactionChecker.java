package com.reliquary.crow.resources;

import net.dv8tion.jda.api.entities.MessageReaction;

import java.util.List;

public class ReactionChecker {

	/*
	checkReaction
	Check the reaction if its a yam reaction
	 */
	public static boolean checkReaction(MessageReaction reaction, String compareReaction) {
		return reaction.getReactionEmote().toString().split(":")[1].toLowerCase()
			.equals(compareReaction);
	}

	/*
	countNumberOfReactions
	Count the number of appearances yam has on a message
	 */
	public static int countNumberOfReactions(List<MessageReaction> reactions, String compareReaction) {

		int count = 0;
		for (MessageReaction reaction : reactions) {
			if (checkReaction(reaction, compareReaction))
				count++;
		}

		return count;
	}

}
