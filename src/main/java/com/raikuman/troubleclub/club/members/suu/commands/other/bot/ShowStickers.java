package com.raikuman.troubleclub.club.members.suu.commands.other.bot;

import com.raikuman.botutilities.commands.manager.CategoryInterface;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.configs.ConfigIO;
import com.raikuman.botutilities.configs.EnvLoader;
import com.raikuman.troubleclub.club.category.OtherCategory;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;

import java.util.Arrays;
import java.util.List;

/**
 * Handles sending the listed stickers in the config to the user
 *
 * @version 1.0 2023-22-02
 * @since 1.0
 */
public class ShowStickers implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		if (!ctx.getEventMember().getId().equals(EnvLoader.get("ownerid")))
			return;

		List<GuildSticker> guildStickers = Arrays.asList(
			ctx.getGuild().getStickerById(ConfigIO.readConfig("troubleclub/dialogue", "dessticker")),
			ctx.getGuild().getStickerById(ConfigIO.readConfig("troubleclub/dialogue", "suusticker")),
			ctx.getGuild().getStickerById(ConfigIO.readConfig("troubleclub/dialogue", "crowsticker")),
			ctx.getGuild().getStickerById(ConfigIO.readConfig("troubleclub/dialogue", "inoristicker"))
		);

		for (GuildSticker guildSticker : guildStickers) {
			ctx.getChannel().asTextChannel().sendStickers(guildSticker).queue();
		}
	}

	@Override
	public String getInvoke() {
		return "showstickers";
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
		return List.of("ss");
	}

	@Override
	public CategoryInterface getCategory() {
		return new OtherCategory();
	}
}
