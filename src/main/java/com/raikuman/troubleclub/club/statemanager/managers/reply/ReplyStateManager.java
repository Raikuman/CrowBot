package com.raikuman.troubleclub.club.statemanager.managers.reply;

import com.raikuman.botutilities.configs.ConfigIO;
import com.raikuman.troubleclub.club.statemanager.CharacterStateManager;
import com.raikuman.troubleclub.club.utilities.CharacterNames;
import com.raikuman.troubleclub.club.utilities.JDAFinder;
import com.raikuman.troubleclub.club.statemanager.JSONInteractionLoader;
import com.raikuman.troubleclub.club.statemanager.managers.reply.objects.ReplyObject;
import kotlin.Pair;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.security.SecureRandom;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Manages replying to users who invoke replies using key words
 *
 * @version 1.0 2023-22-02
 * @since 1.0
 */
public class ReplyStateManager {

	private static ReplyStateManager instance = null;
	private List<ReplyObject> replyObjectList;

	public ReplyStateManager() {
		updateReplyObjects();
	}

	public static ReplyStateManager getInstance() {
		if (instance == null)
			instance = new ReplyStateManager();

		return instance;
	}

	public void updateReplyObjects() {
		this.replyObjectList = JSONInteractionLoader.loadInteractionObjects(ReplyObject.class);
	}

	/**
	 * Handle the reply event
	 * @param event The message received to check replies against
	 */
	public void handleEvent(MessageReceivedEvent event) {
		String message = event.getMessage().getContentRaw();

		if (replyObjectList == null)
			return;

		// Get reply object
		ReplyObject replyObject = checkRepliable(event.getMessage().getContentRaw());
		if (replyObject == null)
			return;

		// Get character name
		CharacterNames targetCharacter = getTargetCharacter(message);

		// Get user's message
		TextChannel userMessageChannel = CharacterStateManager.getInstance().getCharacterStateMap()
				.get(targetCharacter).getJda().getTextChannelById(event.getChannel().getId());
		if (userMessageChannel == null)
			return;

		Message userMessage = userMessageChannel.retrieveMessageById(event.getMessageIdLong()).complete();
		if (userMessage == null)
			return;

		ReplyOperations.handleOperation(replyObject, targetCharacter, userMessage);

		logReplyUpdates(replyObject);
	}

	/**
	 * Get the target character for the reply from the user's message
	 * @param message The message to check for the target character
	 * @return The character enum found in the message
	 */
	private CharacterNames getTargetCharacter(String message) {
		// Get mentioned characters
		List<CharacterNames> mentionedCharacters = new ArrayList<>();
		for (CharacterNames characterName : CharacterNames.values()) {
			List<String> nicknames = JDAFinder.characterNicknames(characterName);
			boolean containsCharacter = message.toLowerCase().contains(characterName.toString().toLowerCase());
			if (!nicknames.isEmpty()) {
				for (String charNickname : nicknames) {
					if (message.toLowerCase().contains(charNickname) || containsCharacter) {
						mentionedCharacters.add(characterName);
					}
				}
			} else {
				if (containsCharacter) {
					mentionedCharacters.add(characterName);
				}
			}
		}

		// Get the target character
		SecureRandom rand = new SecureRandom();
		if (mentionedCharacters.isEmpty())
			return CharacterNames.values()[rand.nextInt(CharacterNames.values().length)];
		else
			return mentionedCharacters.get(rand.nextInt(mentionedCharacters.size()));
	}

	/**
	 * Check if the user's message has the needed key words to initiate the reply
	 * @param message The message to check if it is repliable
	 * @return The found ReplyObject if repliable, otherwise null
	 */
	private ReplyObject checkRepliable(String message) {
		for (ReplyObject replyObject : replyObjectList) {
			int currentWordsMatched = 0;

			// Check for matching words in the message from the reply object
			for (String matchWords : replyObject.matchWords) {
				int matchedWordType = 0;
				String[] wordArray = matchWords.replaceAll("\\s", "").split(",");

				for (String word : wordArray)
					if (message.toLowerCase().contains(word))
						matchedWordType++;

				if (matchedWordType > 0)
					currentWordsMatched++;
			}

			// Set the target reply object
			if (currentWordsMatched == replyObject.matchesNeeded) {
				return replyObject;
			}
		}

		return null;
	}

	/**
	 * Log reply updates
	 * @param replyObject The reply object to log
	 */
	private void logReplyUpdates(ReplyObject replyObject) {
		if (replyObject == null)
			return;

		if (!Boolean.parseBoolean(ConfigIO.readConfig("state", "logreply")))
			return;

		String textChannelId = ConfigIO.readConfig("state", "loggingchannelid");
		if (textChannelId == null)
			return;

		TextChannel textChannel =
			CharacterStateManager.getInstance().getCharacterStateMap()
				.get(CharacterNames.SUU).getJda().getTextChannelById(textChannelId);
		if (textChannel == null)
			return;

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("```md\n");

		stringBuilder.append("[REPLY]");

		stringBuilder
			.append("[")
			.append(replyObject.operationType)
			.append("]");

		// Time
		LocalTime time = LocalTime.now();
		stringBuilder
			.append(time.getHour())
			.append(":")
			.append(time.getMinute())
			.append(":")
			.append(time.getSecond());
		stringBuilder.append("\n");

		stringBuilder.append("```");

		textChannel.sendMessage(stringBuilder).queue();
	}
}
