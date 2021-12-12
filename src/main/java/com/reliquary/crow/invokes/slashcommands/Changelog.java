package com.reliquary.crow.invokes.slashcommands;

import com.reliquary.crow.managers.slashcommands.SlashContext;
import com.reliquary.crow.managers.slashcommands.SlashInterface;
import com.reliquary.crow.resources.other.RandomColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.Button;

/**
 * This class handles sending the links to the Trello and GitHub for the project
 *
 * @version 1.0 2021-12-12
 * @since 1.0
 */
public class Changelog implements SlashInterface {

	@Override
	public void handle(SlashContext ctx) {

		EmbedBuilder builder = new EmbedBuilder()
			.setTitle("Changelog")
			.setColor(RandomColor.getRandomColor());
		StringBuilder descriptionBuilder = builder.getDescriptionBuilder();

		descriptionBuilder
			.append("The buttons below will bring you to the Trello board or the GitHub repo for this " +
				"project!");

		ctx.getEvent().replyEmbeds(builder.build())
			.addActionRow(
				Button.link("https://github.com/Raikuman/CrowBot", "GitHub")
					.withEmoji(Emoji.fromMarkdown("<:github:849286315580719104>")),
				Button.link("https://trello.com/crow169", "Trello")
					.withEmoji(Emoji.fromMarkdown("📃"))
			).queue();

	}

	@Override
	public String getInvoke() {
		return "changelog";
	}

	@Override
	public String getHelp() {
		return "Get the changelog links for this bot";
	}

	@Override
	public String getCategory() {
		return "basic";
	}
}
