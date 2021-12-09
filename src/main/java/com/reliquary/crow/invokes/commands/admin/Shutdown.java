package com.reliquary.crow.invokes.commands.admin;

import com.reliquary.crow.managers.commands.CommandContext;
import com.reliquary.crow.managers.commands.CommandInterface;
import com.reliquary.crow.resources.configs.envConfig;
import me.duncte123.botcommons.BotCommons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Shutdown implements CommandInterface {

	private static final Logger logger = LoggerFactory.getLogger(Shutdown.class);

	@Override
	public void handle(CommandContext ctx) {

		if (ctx.getAuthor().getId().equals(envConfig.get("ownerid"))) {
			logger.info("Shutting down...");

			ctx.getChannel().sendMessage(":sparkles: Goodbye everyone! :sparkles:")
				.queue();

			ctx.getJDA().shutdown();
			BotCommons.shutdown(ctx.getJDA());
			System.exit(0);
		}
	}

	@Override
	public String getInvoke() {
		return "shutdown";
	}

	@Override
	public String getHelp() {
		return "Shuts the bot down";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getCategory() {
		return "admin";
	}
}
