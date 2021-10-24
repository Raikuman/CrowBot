package com.reliquary.crow.slashcommands.manager;

import com.reliquary.crow.commands.manager.CommandContext;

import java.util.List;

public interface SlashInterface {

	void handle(SlashContext ctx);

	String getInvoke();

	default List<String> getAliases() {
		return List.of();
	}
}
