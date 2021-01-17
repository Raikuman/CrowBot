package com.reliquary.crow.commands.fun;

import com.reliquary.crow.resources.RandomClasses.DateAndTime;
import com.reliquary.crow.resources.RandomClasses.RandomColor;
import com.reliquary.crow.resources.TextLineLoader;
import com.reliquary.crow.resources.configs.ConfigHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamBoard {

	private static final Logger logger = LoggerFactory.getLogger(YamBoard.class);
	private static final String DEFAULT_YAMBOARD_LOCATION =
		ConfigHandler.loadConfigSetting("botSettings", "commandresourcedirectory") +
			"/yamboard.txt";

	/*
	checkReaction
	Check the reaction if its a yam reaction
	 */
	public static boolean checkReaction(MessageReaction reaction) {
		return reaction.getReactionEmote().toString().split(":")[1].toLowerCase()
			.equals(ConfigHandler.loadConfigSetting("yamboardSettings", "yamEmoji"));
	}

	/*
	countNumberOfReactions
	Count the number of appearances yam has on a message
	 */
	public static int countNumberOfReactions(List<MessageReaction> reactions) {

		int count = 0;
		for (MessageReaction reaction : reactions) {
			if (checkReaction(reaction))
				count++;
		}

		return count;
	}

	/*
	getPostMessageId
	Returns the yam board embed id using original message id
	 */
	public String getPostMessageId(String key) {

		HashMap<String, String> yamBoardMap = loadYamBoardMap(new File(DEFAULT_YAMBOARD_LOCATION));

		if (yamBoardMap.containsKey(key))
			return yamBoardMap.get(key);

		return null;
	}

	/*
	checkForId
	Check the yam board for the id
	 */
	public static boolean checkForId(String originalId) {
		return loadYamBoardMap(new File(DEFAULT_YAMBOARD_LOCATION)).containsKey(originalId);
	}

	/*
	appendToYamBoard
	Appends ids to yam board file
	 */
	public void appendToYamBoard(String originalId, String postId) {

		HashMap<String, String> yamBoardMap = loadYamBoardMap(new File(DEFAULT_YAMBOARD_LOCATION));
		yamBoardMap.put(originalId, postId);
		writeToYamBoard(yamBoardMap, new File(DEFAULT_YAMBOARD_LOCATION));
	}

	/*
	removeFromYamBoard
	Removes ids from yam board file
	 */
	public void removeFromYamBoard(String originalId) {

		HashMap<String, String> yamBoardMap = loadYamBoardMap(new File(DEFAULT_YAMBOARD_LOCATION));
		yamBoardMap.remove(originalId);
		writeToYamBoard(yamBoardMap, new File(DEFAULT_YAMBOARD_LOCATION));
	}

	/*
	writeToYamBoard
	Write yamboard file using map parameter
	 */
	private void writeToYamBoard(HashMap<String, String> yamBoardMap, File file) {

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));

			for (Map.Entry<String, String> entry : yamBoardMap.entrySet()) {
				writer.write(entry.getKey() + ":" + entry.getValue());
				writer.newLine();
			}

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	loadYamBoardMap
	Loads the yam board from the file to a hashmap
	 */
	private static HashMap<String, String> loadYamBoardMap(File file) {

		HashMap<String, String> yamBoardMap = new HashMap<>();
		String[] splitString;

		// Check if file exists
		if (file.exists()) {
			for (String line : TextLineLoader.readFileToArray(file)) {
				splitString = line.split(":", 1);
				yamBoardMap.put(
					splitString[0],
					splitString[1]
				);
			}
		} else
			createYamBoardFile(file);

		return yamBoardMap;
	}

	/*
	createYamBoardFile
	Writes a yam board file
	 */
	private static void createYamBoardFile(File file) {

		try {
			if (file.createNewFile())
				logger.info("Created file " + file.getName() + " successfully");
			else
				logger.info("Config " + file.getName() + " already created. Continuing...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	createYamBoardEmbed
	Creates the embed for the yam board post
	 */
	public EmbedBuilder createYamBoardEmbed(Message message, String channelName) {

		EmbedBuilder builder = new EmbedBuilder()
			.setAuthor(message.getAuthor().getName(), null, message.getAuthor().getAvatarUrl())
			.setColor(RandomColor.getRandomColor())
			.setDescription(message.getContentRaw())
			.setFooter("#" + channelName + " | " + DateAndTime.getDate() + " " + DateAndTime.getTime());

		// Check for attachments
		if (message.getAttachments().size() > 0)
			builder.setImage(message.getAttachments().get(0).getUrl());

		return builder;
	}
}
