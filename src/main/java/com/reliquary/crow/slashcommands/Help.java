package com.reliquary.crow.slashcommands;

import com.reliquary.crow.slashcommands.manager.SlashContext;
import com.reliquary.crow.slashcommands.manager.SlashInterface;

public class Help implements SlashInterface {

	@Override
	public void handle(SlashContext ctx) {


	}

	@Override
	public String getInvoke() {
		return "help";
	}
}
