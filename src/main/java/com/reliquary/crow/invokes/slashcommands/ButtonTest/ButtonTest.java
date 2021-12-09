package com.reliquary.crow.invokes.slashcommands.ButtonTest;

import com.reliquary.crow.managers.componentmanagers.buttons.ButtonResources;
import com.reliquary.crow.resources.other.RandomColor;
import com.reliquary.crow.managers.slashcommands.SlashContext;
import com.reliquary.crow.managers.slashcommands.SlashInterface;
import net.dv8tion.jda.api.EmbedBuilder;

public class ButtonTest implements SlashInterface {

	@Override
	public void handle(SlashContext ctx) {

		final String userId = ctx.getEvent().getUser().getId();

		// Base test embed
		EmbedBuilder builder = new EmbedBuilder()
			.setTitle("This is a button test")
			.setColor(RandomColor.getRandomColor());
		StringBuilder descriptionBuilder = builder.getDescriptionBuilder();

		descriptionBuilder
			.append("Only a test for buttons");

		ctx.getEvent().replyEmbeds(builder.build())
			.addActionRows(
				ButtonResources.getActionRows(
					ButtonResources.createButtons(userId, ButtonTestResources.getButtonInterfaces(), true)
			)).queue();
	}

	@Override
	public String getInvoke() {
		return "buttontest";
	}

	@Override
	public String getHelp() {
		return "A button test";
	}

	@Override
	public String getCategory() {
		return "basic";
	}
}
