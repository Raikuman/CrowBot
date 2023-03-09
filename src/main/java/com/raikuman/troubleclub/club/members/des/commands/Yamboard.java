package com.raikuman.troubleclub.club.members.des.commands;

import com.raikuman.botutilities.commands.manager.CategoryInterface;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.troubleclub.club.category.FunCategory;

/**
 * Handles providing the help command with yamboard descriptions
 *
 * @version 1.0 2023-09-03
 * @since 1.0
 */
public class Yamboard implements CommandInterface {

	@Override
	public void handle(CommandContext commandContext) {}

	@Override
	public String getInvoke() {
		return "React to a message with \uD83C\uDF60";
	}

	@Override
	public String getUsage() {
		return "<\uD83C\uDF60>";
	}

	@Override
	public String getDescription() {
		return "Posts the message to the yamboard";
	}

	@Override
	public CategoryInterface getCategory() {
		return new FunCategory();
	}
}
