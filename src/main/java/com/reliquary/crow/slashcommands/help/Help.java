package com.reliquary.crow.slashcommands.help;

import com.reliquary.crow.componentmanagers.selectionmenus.SelectResources;
import com.reliquary.crow.slashcommands.help.resources.HelpResources;
import com.reliquary.crow.slashcommands.manager.SlashContext;
import com.reliquary.crow.slashcommands.manager.SlashInterface;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;

/**
 * This class handles getting a list of commands and their description to provide a help menu
 *
 * @version 1.0 2021-09-12
 * @since 1.0
 */
public class Help implements SlashInterface {

	@Override
	public void handle(SlashContext ctx) {

		final String userId = ctx.getEvent().getUser().getId();

		SelectionMenu menu = SelectionMenu.create("menu:class")
			.setPlaceholder("View commands in category")
			.setRequiredRange(1, 1)
			.addOptions(SelectResources.createSelectOptions(userId, HelpResources.getSelectionMenuInterfaces()))
			.build();


		// Reply with selection menu
		ctx.getEvent().replyEmbeds(
			HelpResources.provideHelpHomeEmbed().build()
		).addActionRow(menu).queue();
	}

	@Override
	public String getInvoke() {
		return "help";
	}

	@Override
	public String getHelp() {
		return "A little help for the uninitiated";
	}

	@Override
	public String getCategory() {
		return "";
	}
}
