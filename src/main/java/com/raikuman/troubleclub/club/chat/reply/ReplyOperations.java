package com.raikuman.troubleclub.club.chat.reply;

import com.raikuman.botutilities.configs.ConfigIO;
import com.raikuman.troubleclub.club.chat.reply.objects.ReplyCharacterObject;
import kotlin.Pair;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.security.SecureRandom;

/**
 * Handles operations for replies
 *
 * @version 1.1 2023-21-01
 * @since 1.0
 */
public class ReplyOperations {

	/**
	 * Handles the reply operation depending on the reply enum
	 * @param handleObject The handle object to get information to handle with
	 * @param jdaPair The JDA to use when handling the object
	 * @param targetMessage The target message to reply to
	 */
	public static void handleOperation(HandleObject handleObject, Pair<String, JDA> jdaPair,
		Message targetMessage) {
		OperationObject operationObject = new OperationObject(handleObject, jdaPair, targetMessage);
		switch (handleObject.replyObject.operationType) {
			case MUSIC:
				handleMusic(operationObject);
		}
	}

	/**
	 * Handles music reply operations
	 * @param operationObject The operation object with data to handle
	 */
	private static void handleMusic(OperationObject operationObject) {
		SecureRandom rand = new SecureRandom();
		ReplyCharacterObject characterObject = operationObject.characterObject;

		// Check if the operation could be done
		boolean doOperation = true;

		// Get member to check if they are in voice chat
		Member member = operationObject.targetMessage.getMember();
		if (member == null)
			doOperation = false;

		// Get voice state
		GuildVoiceState memberVoiceState = null;
		if (doOperation) memberVoiceState = member.getVoiceState();
		if (memberVoiceState == null)
			doOperation = false;

		// Check if user is in voice channel
		if (doOperation && !memberVoiceState.inAudioChannel())
			doOperation = false;

		TextChannel operationChannel = operationObject.targetMessage.getGuild().getTextChannelById(
			ConfigIO.readConfig("chat", "replycommandchannelid")
		);

		// Handle if operation could not be done
		if (operationChannel == null || !doOperation) {
			operationObject.targetMessage.reply(
				characterObject.unableDialogue.get(
					rand.nextInt(characterObject.unableDialogue.size())
				)
			).queue();
			return;
		}

		// Check for reaction
		if (!characterObject.emoji.isEmpty())
			operationObject.targetMessage.addReaction(Emoji.fromFormatted(characterObject.emoji)).queue();

		// Send reply message
		operationObject.targetMessage.reply(
			characterObject.dialogue.get(rand.nextInt(characterObject.dialogue.size()))
		).queue();

		// Send operation command
		operationChannel.sendMessage(
			operationObject.operation + " " +
				operationObject.targetMessage.getMember().getIdLong()
		).queue();
	}
}

/**
 * An object to hold information on handling the reply operation
 *
 * @version 1.0 2023-20-01
 * @since 1.0
 */
class OperationObject {
	public final ReplyCharacterObject characterObject;
	public final Message targetMessage;
	public final String operation;

	public OperationObject(HandleObject handleObject, Pair<String, JDA> jdaPair, Message targetMessage) {
		this.targetMessage = targetMessage;

		// Get target character reply object
		this.characterObject = handleObject.replyObject.replyCharacterMap
			.get(handleObject.targetCharacter);

		// Handle operations
		this.operation = characterObject.operation;
	}
}
