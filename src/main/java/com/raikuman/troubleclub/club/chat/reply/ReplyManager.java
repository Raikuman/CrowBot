package com.raikuman.troubleclub.club.chat.reply;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raikuman.troubleclub.club.chat.reply.objects.ReplyObject;
import com.raikuman.troubleclub.club.main.JDAFinder;
import kotlin.Pair;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;

/**
 * Manages replying to a user message
 *
 * @version 1.0 2023-18-01
 * @since 1.0
 */
public class ReplyManager {

	private static final Logger logger = LoggerFactory.getLogger(ReplyManager.class);

	private static ReplyManager instance = null;
	private List<ReplyObject> replyObjectList;

	public ReplyManager() {
		loadReplyJsons();
	}

	public static ReplyManager getInstance() {
		if (instance == null)
			instance = new ReplyManager();

		return instance;
	}

	/**
	 * Checks the current message if it is good to reply to or not
	 * @param message The message to check against
	 * @return A handle object with information containing if the message is reply-able
	 */
	private HandleObject checkMessage(String message) {
		// Check number of matches in message
		ReplyObject targetReplyObject = null;
		for (ReplyObject replyObject : replyObjectList) {
			int currentWordsMatched = 0;
			for (String matchWords : replyObject.matchWords) {
				int matchedWordType = 0;
				String[] wordArray = matchWords
					.replaceAll("\\s", "")
					.split(",");

				for (String word : wordArray)
					if (message.toLowerCase().contains(word))
						matchedWordType++;

				if (matchedWordType > 0)
					currentWordsMatched++;
			}

			if (currentWordsMatched == replyObject.matchesNeeded) {
				targetReplyObject = replyObject;
				break;
			}
		}

		// No matches
		if (targetReplyObject == null)
			return new HandleObject(false, "", null);

		// Get mentioned characters
		List<String> mentionedCharacters = new ArrayList<>();
		for (String character : JDAFinder.characterNames()) {
			if (message.toLowerCase().contains(character))
				mentionedCharacters.add(character);
		}

		String targetCharacter;
		SecureRandom rand = new SecureRandom();
		if (mentionedCharacters.isEmpty())
			targetCharacter = JDAFinder.characterNames().get(rand.nextInt(JDAFinder.characterNames().size()));
		else
			targetCharacter = mentionedCharacters.get(rand.nextInt(mentionedCharacters.size()));

		return new HandleObject(true, targetCharacter, targetReplyObject);
	}

	/**
	 * Handles the message event and checks if the event can be replied to with the following reply objects
	 * @param event The event to check if it is reply-able
	 */
	public void handleEvent(MessageReceivedEvent event) {
		HandleObject handleObject = checkMessage(event.getMessage().getContentRaw());

		// Check if the message is good to reply to
		if (!handleObject.handleMessage)
			return;

		// Check JDA from target character
		Pair<String, JDA> jdaPair = JDAFinder.getInstance().getJDA(handleObject.targetCharacter);
		if (jdaPair.getSecond() == null)
			return;

		// Get target channel of the user's message using target JDA
		TextChannel targetChannel = jdaPair.getSecond().getTextChannelById(event.getChannel().getIdLong());
		if (targetChannel == null)
			return;

		// Get target message for the selected JDA to reply to
		Message targetMessage = targetChannel.retrieveMessageById(event.getMessageIdLong()).complete();
		if (targetMessage == null)
			return;

		ReplyOperations.handleOperation(handleObject, jdaPair, targetMessage);
	}

	/**
	 * Load the reply data jsons to memory
	 */
	public void loadReplyJsons() {
		File directory = new File("resources/reply");
		if (!directory.exists()) {
			if (!directory.mkdirs()) {
				logger.error("Could not create reply directory");
				return;
			}
		}

		if (!directory.isDirectory()) {
			logger.error("Reply file is not a directory");
			return;
		}

		File[] directoryFiles = directory.listFiles();
		if (directoryFiles == null) {
			logger.error("Path  or I/O error, could not load files from directory");
			return;
		}

		// Get objects from jsons
		List<ReplyObject> replyObjectList = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();
		for (File file : directoryFiles) {
			try {
				replyObjectList.add(objectMapper.readValue(file, ReplyObject.class));
			} catch (IOException e) {
				logger.error("Could not read the reply json");
			}
		}

		this.replyObjectList = replyObjectList;
	}
}

/**
 * An object to hold information on how to handle a reply
 *
 * @version 1.0 2023-20-01
 * @since 1.0
 */
class HandleObject {

	public boolean handleMessage;
	public String targetCharacter;
	public ReplyObject replyObject;

	public HandleObject(boolean handleMessage, String targetCharacter, ReplyObject replyObject) {
		this.handleMessage = handleMessage;
		this.targetCharacter = targetCharacter;
		this.replyObject = replyObject;
	}
}
