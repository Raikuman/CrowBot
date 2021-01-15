package com.reliquary.crow.commands.dnd;

import com.reliquary.crow.commands.manager.CommandContext;
import com.reliquary.crow.commands.manager.CommandInterface;
import com.reliquary.crow.resources.RandomClasses.RandomColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class Dice implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {

		List<String> args = ctx.getArgs();
		TextChannel channel = ctx.getChannel();

		EmbedBuilder builder = new EmbedBuilder()
			.setTitle("")
			.setColor(RandomColor.getRandomColor());
		StringBuilder descriptionBuilder =  builder.getDescriptionBuilder();

		if (args.isEmpty()) {

		} else {

		}

	}

	@Override
	public String getInvoke() {
		return "dice";
	}

	@Override
	public String getHelp() {
		return "Rolls a dice (up to 100 dice at once)";
	}

	@Override
	public String getUsage() {
		return "null";
	}

	@Override
	public String getCategory() {
		return "dnd";
	}
}
