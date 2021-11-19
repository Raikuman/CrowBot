package com.reliquary.crow.resources.jda;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.Duration;

/**
 * This class handles methods with sending JDA messages
 *
 * @version 1.0.1
 * @since 2021-19-11
 */
public class MessageResources {

	/**
	 * This method sends a timed message to a specified text channel
	 * @param message Provides the message to send
	 * @param channel Provides the text channel to send the message in
	 * @param numSeconds Provides the number of seconds to delete the message after
	 */
	public static void timedMessage(String message, TextChannel channel, int numSeconds) {
		channel.sendMessage(message)
			.delay(Duration.ofSeconds(numSeconds))
			.flatMap(Message::delete)
			.queue();
	}

}
