package com.reliquary.crow.commands.settings;

import com.reliquary.crow.commands.manager.CommandContext;
import com.reliquary.crow.commands.manager.CommandInterface;
import com.reliquary.crow.resources.RandomClasses.RandomColor;
import com.reliquary.crow.resources.configs.ConfigHandler;
import com.reliquary.crow.resources.configs.envConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class ChangePrefix implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {

		List<String> args = ctx.getArgs();
		TextChannel channel = ctx.getChannel();

		EmbedBuilder builder = new EmbedBuilder()
			.setColor(RandomColor.getRandomColor());
		StringBuilder descriptionBuilder =  builder.getDescriptionBuilder();

		// Check arguments
		if (!args.isEmpty()) {
			ConfigHandler configHandler = new ConfigHandler();
			configHandler.writeConfigSetting(
				"botSettings",
				"prefix",
				args.get(0)
			);

			descriptionBuilder
				.append("```md\n")
				.append("Prefix changed to ")
				.append("<")
				.append(args.get(0))
				.append(">");

		} else {
			descriptionBuilder
				.append("You must input an argument to change the prefix!");
		}

		channel.sendMessage(builder.build())
			.queue();
	}

	@Override
	public String getInvoke() {
		return "changeprefix";
	}

	@Override
	public String getHelp() {
		ConfigHandler configHandler = new ConfigHandler();
		return "Changes the prefix for commands. Current prefix: '"
			+ configHandler.loadConfigSetting("botSettings", "prefix");
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getCategory() {
		return "settings";
	}
}
