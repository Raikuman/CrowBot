package com.reliquary.crow.invokes.commands.basic;

import com.reliquary.crow.managers.commands.CommandContext;
import com.reliquary.crow.managers.commands.CommandInterface;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;

import java.time.Duration;

/**
 * This class shows the gateway ping of the bot
 *
 * @version 1.0 2021-16-12
 * @since 1.0
 */
public class Ping implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		// Get JDA object to find rest ping
		JDA jda = ctx.getJDA();

		jda.getRestPing().queue(
			(ping) -> ctx.getChannel()
				.sendMessageFormat("Pong!: %sms\nGateway ping: %sms",
					ping, jda.getGatewayPing())
						.delay(Duration.ofSeconds(30))
						.flatMap(Message::delete)
						.queue()
		);
	}

	@Override
	public String getInvoke() {
		return "ping";
	}

	@Override
	public String getHelp() {
		return "A simple ping command (from Discord to bot)";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getCategory() {
		return "basic";
	}
}
