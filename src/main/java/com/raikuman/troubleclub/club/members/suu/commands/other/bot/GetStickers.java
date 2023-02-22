package com.raikuman.troubleclub.club.members.suu.commands.other.bot;

import com.raikuman.botutilities.commands.manager.CategoryInterface;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.configs.EnvLoader;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.troubleclub.club.category.OtherCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;

import java.util.List;

/**
 * Handles sending name and id of all stickers in the guild
 *
 * @version 1.0 2023-22-02
 * @since 1.0
 */
public class GetStickers implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		if (!ctx.getEventMember().getId().equals(EnvLoader.get("ownerid")))
			return;

		EmbedBuilder builder = new EmbedBuilder()
			.setTitle(ctx.getGuild().getName() + " Stickers")
			.setColor(RandomColor.getRandomColor());
		StringBuilder descriptionBuilder = builder.getDescriptionBuilder();
		descriptionBuilder.append("```\n");

		for (GuildSticker guildSticker : ctx.getGuild().getStickers()) {
			descriptionBuilder.append(guildSticker.getName());
			descriptionBuilder.append(" | ");
			descriptionBuilder.append(guildSticker.getId());
			descriptionBuilder.append("\n");
		}

		descriptionBuilder.append("```\n");

		ctx.getChannel().asTextChannel().sendMessageEmbeds(builder.build()).queue();
	}

	@Override
	public String getInvoke() {
		return "getstickers";
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
		return List.of("gs");
	}

	@Override
	public CategoryInterface getCategory() {
		return new OtherCategory();
	}
}
