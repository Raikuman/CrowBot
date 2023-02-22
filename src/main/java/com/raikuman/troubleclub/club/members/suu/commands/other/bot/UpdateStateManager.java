package com.raikuman.troubleclub.club.members.suu.commands.other.bot;

import com.raikuman.botutilities.commands.manager.CategoryInterface;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.configs.EnvLoader;
import com.raikuman.troubleclub.club.category.OtherCategory;
import com.raikuman.troubleclub.club.statemanager.managers.dialogue.DialogueStateManager;
import com.raikuman.troubleclub.club.statemanager.managers.reply.ReplyStateManager;
import com.raikuman.troubleclub.club.statemanager.managers.status.StatusStateManager;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Handles updating the reply manager's json reply data
 *
 * @version 1.1 2023-22-01
 * @since 1.0
 */
public class UpdateStateManager implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		if (!ctx.getEventMember().getId().equals(EnvLoader.get("ownerid")))
			return;

		DialogueStateManager.getInstance().updateDialogueObjects();
		StatusStateManager.getInstance().updateStatusObjects();
		ReplyStateManager.getInstance().updateReplyObjects();

		ctx.getEvent().getMessage().addReaction(Emoji.fromFormatted("\uD83D\uDC4D")).queue();
		ctx.getEvent().getMessage().delete().queueAfter(1, TimeUnit.SECONDS);
	}

	@Override
	public String getInvoke() {
		return "updatereplymanager";
	}

	@Override
	public String getUsage() {
		return "a";
	}

	@Override
	public String getDescription() {
		return "b";
	}

	@Override
	public List<String> getAliases() {
		return List.of("updatereply", "ur", "urm");
	}

	@Override
	public CategoryInterface getCategory() {
		return new OtherCategory();
	}
}
