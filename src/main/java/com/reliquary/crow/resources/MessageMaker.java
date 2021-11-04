package com.reliquary.crow.resources;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.Duration;

public class MessageMaker {

	public static void timedMessage(String message, TextChannel channel, int numSeconds) {
		channel.sendMessage(message)
			.delay(Duration.ofSeconds(numSeconds))
			.flatMap(Message::delete)
			.queue();
	}

}
