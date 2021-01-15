package com.reliquary.crow.commands.manager;

import java.util.List;

public interface CommandInterface {

	void handle(CommandContext ctx);

	String getInvoke();

	String getHelp();

	String getUsage();

	String getCategory();

	default List<String> getAliases() {
		return List.of();
	}
}
