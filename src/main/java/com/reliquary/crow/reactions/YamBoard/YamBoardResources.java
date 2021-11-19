package com.reliquary.crow.reactions.YamBoard;

import com.reliquary.crow.resources.RandomClasses.DateAndTime;
import com.reliquary.crow.resources.RandomClasses.RandomColor;
import com.reliquary.crow.resources.TextLineLoader;
import com.reliquary.crow.resources.configs.DefaultConfigWriter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a resources class for Yamboard.java, where all file writing and embed editing occurs.
 * This class must give methods to:
 * 1. Append board ids to the yamboard file
 * 2. Delete board ids from the yamboard file
 * 3. Get the embed when creating a yamboard
 * Other methods found in this class are mostly used to serve these 3 main methods
 *
 * @version 1.0
 * @since 2021-19-11
 */
public class YamBoardResources {

	private final Logger logger = LoggerFactory.getLogger(DefaultConfigWriter.class);

	private static final int MAX_BOARD_IDS = 10;

	/**
	 * This method loads all the message/board ids from the yamboard file into a hashmap for use
	 * @param boardFile Provides the yamboard file for reading
	 * @return Returns the hashmap of all the message/board ids
	 */
	protected HashMap<String, String> loadBoardIds(File boardFile) {

		HashMap<String, String> idMap = new HashMap<>();

		List<String> boardIds = TextLineLoader.readFileToArray(boardFile);

		for (String board : boardIds)
			idMap.put(board.split(":")[0], board.split(":")[1]);

		return idMap;
	}

	/**
	 * This method takes all the provided message/board ids and writes them to the yamboard file
	 * @param boardIds Provides the board ids map to write to the yamboard file
	 * @param boardFile Provides the yamboard file for reading
	 */
	protected void writeBoardIds(HashMap<String, String> boardIds, File boardFile) {

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(boardFile, false));

			// Write to file in 'messageId:boardId'
			int count = 1;
			for (Map.Entry<String, String> entry : boardIds.entrySet()) {
				writer.write(entry.getKey() + ":" + entry.getValue());
				writer.newLine();

				count++;
				if (count == MAX_BOARD_IDS)
					break;
			}

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method appends a new message/board id to the yamboard file
	 * @param messageId Provides the message id of the reacted message
	 * @param boardId Provides the board id of the sent embed
	 * @param boardFile Provides the board ids map to write to the yamboard file
	 */
	protected void appendBoardId(String messageId, String boardId, File boardFile) {

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(boardFile, true));

			// Append to file in 'messageId:boardId'
			writer.append(messageId).append(":").append(boardId);
			writer.newLine();

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method gets the board id from the yamboard file from the message id
	 * @param messageId Provides the message id of the reacted message
	 * @param boardFile Provides the board ids map to write to the yamboard file
	 * @return Returns the board id
	 */
	protected String getBoardId(String messageId, File boardFile) {

		if (loadBoardIds(boardFile).containsKey(messageId))
			return loadBoardIds(boardFile).get(messageId);

		return null;
	}

	/**
	 * This method deletes the message/board id using the message id from the hashmap and rewrites the file
	 * @param messageId Provides the message id of the reacted message
	 * @param boardFile Provides the board ids map to write to the yamboard file
	 */
	protected void deleteBoardId(String messageId, File boardFile) {

		HashMap<String, String> boardIds = loadBoardIds(boardFile);
		boardIds.remove(messageId);
		writeBoardIds(boardIds, boardFile);
	}

	/**
	 * This method creates an EmbedBuilder object using information from the reacted message
	 * @param user Provides the user for information in the embed
	 * @param textChannel Provides the text channel for information in the embed
	 * @param message Provides the message object for embed description/checking url/and attachments
	 * @return Returns the EmbedBuilder that has been edited
	 */
	protected EmbedBuilder buildEmbed(User user, TextChannel textChannel, Message message) {

		EmbedBuilder builder = new EmbedBuilder()
			.setAuthor(user.getName(), null, user.getAvatarUrl())
			.setColor(RandomColor.getRandomColor())
			.setFooter("#" + textChannel.getName() + " | " + DateAndTime.getDate() + " " + DateAndTime.getTime());

		// Check the url if it provides a direct link to a gif
		if (checkIfGif(message.getContentRaw()))
			builder.setImage(message.getContentRaw());
		else
			builder.setDescription(message.getContentRaw());

		// Get first attachment of message and add to embed
		if (message.getAttachments().size() > 0) {
			builder.setImage(message.getAttachments().get(0).getUrl());
			builder.setDescription(message.getContentRaw());
		}

		return builder;
	}

	/**
	 * This method checks if the provided string is a URL, which then checks if it is a GIF by checking the
	 * first 3 bytes
	 * @param path Provides the path string of the message
	 * @return Returns a boolean whether path is a gif or not
	 */
	protected boolean checkIfGif(String path) {

		// Check if path is direct link by checking first 3 bytes for GIF
		try {
			// Get stream from uri
			InputStream input = new URL(path).openStream();

			byte[] bytes = IOUtils.toByteArray(input);

			// Check first 3 bytes
			if (bytes[0] != 71)
				return false;

			if (bytes[1] != 73)
				return false;

			return bytes[2] == 70;
		} catch (IOException e) {
			logger.info("Send plaintext board");
		}

		return false;
	}
}
