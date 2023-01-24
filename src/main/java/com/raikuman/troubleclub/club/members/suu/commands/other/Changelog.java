package com.raikuman.troubleclub.club.members.suu.commands.other;

import com.raikuman.botutilities.commands.manager.CategoryInterface;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.troubleclub.club.category.OtherCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.List;

/**
 * Handles sending an embed with informational links about the bot
 *
 * @version 1.0 2023-23-01
 * @since 1.0
 */
public class Changelog implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		EmbedBuilder builder = new EmbedBuilder()
			.setAuthor("Changelog", null, ctx.getEvent().getAuthor().getEffectiveAvatarUrl())
			.setColor(RandomColor.getRandomColor())
			.setDescription("The buttons below will bring you to the Trello board or the GitHub repo for this " +
				"project!");

		ctx.getChannel().sendMessageEmbeds(builder.build())
			.setActionRow(
				Button.link("https://github.com/Raikuman/TroubleClub-Bots", "GitHub")
					.withEmoji(Emoji.fromFormatted("<:github:849286315580719104>")),
				Button.link("https://trello.com/b/ooQvjm9w/trouble-club", "Trello")
					.withEmoji(Emoji.fromFormatted("📃"))
			).queue();

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "clublog";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Useful links for what's going on in our lives!";
	}

	@Override
	public List<String> getAliases() {
		return List.of(
			"clog",
			"ccl",
			"clubl"
		);
	}

	@Override
	public CategoryInterface getCategory() {
		return new OtherCategory();
	}
}
