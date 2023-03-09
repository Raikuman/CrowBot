package com.raikuman.troubleclub.club.members.suu.commands.other.bot;

import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.configs.EnvLoader;
import com.raikuman.troubleclub.club.utilities.CharacterNames;
import com.raikuman.troubleclub.club.utilities.JDAFinder;

import java.util.List;

/**
 * Handles shutting down the bot
 *
 * @version 1.1 2023-08-03
 * @since 1.0
 */
public class BotShutdown implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		if (!ctx.getEventMember().getId().equals(EnvLoader.get("ownerid")))
			return;

		for (CharacterNames characterName : CharacterNames.values())
			JDAFinder.getInstance().getJDA(characterName).shutdownNow();

		System.exit(0);
	}

	@Override
	public String getInvoke() {
		return "botshutdown";
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
		return List.of("bs");
	}
}
