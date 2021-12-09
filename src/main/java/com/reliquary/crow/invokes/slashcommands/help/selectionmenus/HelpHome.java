package com.reliquary.crow.invokes.slashcommands.help.selectionmenus;

import com.reliquary.crow.managers.componentmanagers.selectionmenus.SelectContext;
import com.reliquary.crow.managers.componentmanagers.selectionmenus.SelectInterface;
import com.reliquary.crow.invokes.slashcommands.help.resources.HelpResources;

/**
 * This class provides the SelectInterface for the base help embed in the help command
 *
 * @version 1.0 2021-09-12
 * @since 1.0
 */
public class HelpHome implements SelectInterface {

	@Override
	public void handle(SelectContext ctx) {

		ctx.getEvent().editMessageEmbeds(
			HelpResources.provideHelpHomeEmbed().build()
		).queue();
	}

	@Override
	public String getMenuValue() {
		return "helphome";
	}

	@Override
	public String getLabel() {
		return "Home";
	}
}
