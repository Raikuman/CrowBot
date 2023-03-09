package com.raikuman.troubleclub.club.members.suu.commands.other.bot;

import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.configs.ConfigIO;
import com.raikuman.botutilities.configs.EnvLoader;
import com.raikuman.troubleclub.club.statemanager.managers.voice.IdleStateManager;

import java.util.List;

/**
 * Handles force running the idling state
 *
 * @version 1.1 2023-08-03
 * @since 1.0
 */
public class RunIdling implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		if (Boolean.parseBoolean(ConfigIO.readConfig("state", "disableStateCommands")))
			return;

		if (!ctx.getEventMember().getId().equals(EnvLoader.get("ownerid")))
			return;

		IdleStateManager.getInstance().updateIdlingState();
	}

	@Override
	public String getInvoke() {
		return "runidling";
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
		return List.of("ri");
	}
}
