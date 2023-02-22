package com.raikuman.troubleclub.club.statemanager.managers.reply;

import com.raikuman.botutilities.configs.ConfigIO;
import com.raikuman.troubleclub.club.statemanager.CharacterStateManager;
import com.raikuman.troubleclub.club.statemanager.managers.reply.objects.ReplyCharacterObject;
import com.raikuman.troubleclub.club.statemanager.managers.reply.objects.ReplyObject;
import com.raikuman.troubleclub.club.utilities.CharacterNames;
import kotlin.Pair;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.security.SecureRandom;

/**
 * Operations for all reply types
 *
 * @version 1.0 2023-22-02
 * @since 1.0
 */
public class ReplyOperations {

	/**
	 * Handles replying to the user with the correct operation type
	 * @param replyObject The reply object to operate on
	 * @param characterName The character name that will reply
	 * @param userMessage The user message to reply to
	 */
	public static void handleOperation(ReplyObject replyObject, CharacterNames characterName, Message userMessage) {
		ReplyOperationObject operationObject = new ReplyOperationObject(replyObject, characterName, userMessage);
		switch (replyObject.operationType) {
			case REPLY:
				handleReply(operationObject);
				break;

			case MUSIC:
				handleMusic(operationObject);
				break;
		}
	}

	/**
	 * Handle replying with just a message
	 * @param operationObject The reply operation object to reply with
	 */
	public static void handleReply(ReplyOperationObject operationObject) {
		// Handle operation
		SecureRandom rand = new SecureRandom();
		ReplyCharacterObject characterObject = operationObject.characterObject;
		if (!operationObject.characterObject.emoji.isEmpty())
			operationObject.userMessage.addReaction(Emoji.fromFormatted(characterObject.emoji)).queue();

		operationObject.userMessage.reply(
			characterObject.dialogue.get(rand.nextInt(characterObject.dialogue.size()))
		).queue();
	}

	/**
	 * Handle replying with a message and playing the character's playlist
	 * @param operationObject The reply operation object to reply with
	 */
	public static void handleMusic(ReplyOperationObject operationObject) {
		// Check user's voice state
		Member member = operationObject.userMessage.getMember();
		if (member == null)
			return;

		boolean doOperation = true;
		GuildVoiceState memberVoiceState = member.getVoiceState();
		if (memberVoiceState == null)
			doOperation = false;

		if (doOperation && !memberVoiceState.inAudioChannel())
			doOperation = false;

		// Handle if operation could not be done
		SecureRandom rand = new SecureRandom();
		ReplyCharacterObject characterObject = operationObject.characterObject;
		if (operationObject.operationChannel == null || !doOperation) {
			operationObject.userMessage.reply(
				characterObject.unableDialogue.get(
					rand.nextInt(characterObject.unableDialogue.size())
				)
			).queue();
			return;
		}

		// Handle operation
		if (!operationObject.characterObject.emoji.isEmpty())
			operationObject.userMessage.addReaction(Emoji.fromFormatted(characterObject.emoji)).queue();

		operationObject.userMessage.reply(
			characterObject.dialogue.get(rand.nextInt(characterObject.dialogue.size()))
		).queue();

		// Send operation command
		operationObject.operationChannel.sendMessage(
			operationObject.operation + " " +
			operationObject.userMessage.getMember().getIdLong()
		).queue();
	}
}

/**
 * The reply operation object containing all the data to handle a reply
 *
 * @version 1.0 2023-20-02
 * @since 1.0
 */
class ReplyOperationObject {
	public final ReplyCharacterObject characterObject;
	public final Message userMessage;
	public final TextChannel operationChannel;
	public final String operation;

	public ReplyOperationObject(ReplyObject replyObject, CharacterNames characterName, Message userMessage) {
		this.userMessage = userMessage;

		// Get target reply character object
		this.characterObject = replyObject.replyCharacterMap.get(characterName);

		// Get operation
		this.operation = characterObject.operation;

		// Get operation channel
		operationChannel =
			CharacterStateManager.getInstance().getCharacterStateMap().get(characterName).getJda()
			.getTextChannelById(ConfigIO.readConfig("troubleclub/reply", "textchannelid"));
	}
}
