package com.raikuman.troubleclub.club.members.des.yamboard.buttons;

import com.raikuman.botutilities.buttons.manager.ButtonContext;
import com.raikuman.botutilities.buttons.manager.ButtonInterface;
import com.raikuman.troubleclub.club.members.des.yamboard.KarmaUtilities;
import net.dv8tion.jda.api.entities.emoji.Emoji;

/**
 * Handles upvoting a yamboard post
 *
 * @version 1.0 2023-09-03
 * @since 1.0
 */
public class Upvote implements ButtonInterface {

	@Override
	public void handle(ButtonContext ctx) {
		KarmaUtilities.changeKarma(
			ctx,
			true);
	}

	@Override
	public String getButtonId() {
		return "upvote";
	}

	@Override
	public Emoji getEmoji() {
		return Emoji.fromFormatted("\uD83D\uDD3C");
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public boolean bypassAuthorCheck() {
		return true;
	}
}
