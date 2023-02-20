package com.raikuman.troubleclub.club.members.suu.commands.other.bot;

import com.raikuman.botutilities.commands.manager.CategoryInterface;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.configs.ConfigIO;
import com.raikuman.botutilities.configs.EnvLoader;
import com.raikuman.troubleclub.club.category.OtherCategory;
import com.raikuman.troubleclub.club.statemanager.managers.dialogue.DialogueStateManager;

import java.util.List;

/**
 * Handles force running the dialogue state
 *
 * @version 1.0 2023-19-02
 * @since 1.0
 */
public class RunDialogue implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		if (Boolean.parseBoolean(ConfigIO.readConfig("state", "disableStateCommands")))
			return;

		if (!ctx.getEventMember().getId().equals(EnvLoader.get("ownerid")))
			return;

		DialogueStateManager.getInstance().updateDialogueState();
	}

	@Override
	public String getInvoke() {
		return "rundialogue";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public List<String> getAliases() {
		return List.of("rd");
	}

	@Override
	public CategoryInterface getCategory() {
		return new OtherCategory();
	}
}
