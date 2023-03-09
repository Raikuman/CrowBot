package com.raikuman.troubleclub.club.members.des.yamboard;

import com.raikuman.botutilities.buttons.manager.ButtonContext;
import com.raikuman.botutilities.configs.ConfigIO;
import net.dv8tion.jda.api.entities.Member;

/**
 * Handles managing the karma when a user upvotes/downvotes a post
 *
 * @version 1.0 2023-09-03
 * @since 1.0
 */
public class KarmaUtilities {

	public static void changeKarma(ButtonContext ctx, boolean addKarma) {
		Member eventMember = ctx.getEventMember();
		long memberId = eventMember.getIdLong();
		long postMessageId = ctx.getEvent().getMessageIdLong();

		YamboardMessage yamboardMessage = YamboardManager.getInstance().getMessageFromPost(postMessageId);

		boolean selfKarma = Boolean.parseBoolean(ConfigIO.readConfig("yamboard", "enableselfkarma"));
		if (!selfKarma) {
			if (memberId == yamboardMessage.getPosterId()) {
				ctx.getEvent().deferEdit().queue();
				return;
			}
		}

		if (yamboardMessage == null) {
			ctx.getEvent().deferEdit().queue();
			return;
		}

		if (addKarma) {
			if (yamboardMessage.checkVoterVoted(memberId, true))
				yamboardMessage.removeUpvote(memberId);
			else
				yamboardMessage.addUpvote(memberId);
		} else {
			if (yamboardMessage.checkVoterVoted(memberId, false))
				yamboardMessage.removeDownvote(memberId);
			else
				yamboardMessage.addDownvote(memberId);
		}

		yamboardMessage.updateEmbed(ctx.getEvent());
	}
}
