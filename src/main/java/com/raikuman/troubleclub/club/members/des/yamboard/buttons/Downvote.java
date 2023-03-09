package com.raikuman.troubleclub.club.members.des.yamboard.buttons;

import com.raikuman.botutilities.buttons.manager.ButtonContext;
import com.raikuman.botutilities.buttons.manager.ButtonInterface;
import com.raikuman.botutilities.configs.ConfigIO;
import com.raikuman.troubleclub.club.members.des.yamboard.KarmaUtilities;
import net.dv8tion.jda.api.entities.emoji.Emoji;

/**
 * Handles downvoting a yamboard post
 *
 * @version 1.0 2023-09-03
 * @since 1.0
 */
public class Downvote implements ButtonInterface {

	@Override
	public void handle(ButtonContext ctx) {
		KarmaUtilities.changeKarma(
			ctx,
			false);
	}

	@Override
	public String getButtonId() {
		return "downvote";
	}

	@Override
	public Emoji getEmoji() {
		return Emoji.fromFormatted("\uD83D\uDD3D");
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
