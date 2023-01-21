package com.raikuman.troubleclub.club.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Handles loading jsons for chat functionality
 *
 * @version 1.0 2023-20-01
 * @since 1.0
 */
public class ChatJSONLoader {

	public static final Logger logger = LoggerFactory.getLogger(ChatJSONLoader.class);

	/**
	 * Load chat files from resource folder
	 *
	 * @param type The type of chat to retrieve
	 * @return The array of files of the respective type
	 */
	public static File[] getJsonFiles(String type) {
		ChatEnum chatEnum = null;
		switch (type) {
			case "reply":
				chatEnum = ChatEnum.reply;
				break;

			case "dialogue":
				chatEnum = ChatEnum.dialogue;
				break;
		}

		if (chatEnum == null)
			return null;

		File directory = new File("resources/" + chatEnum.name());
		if (!directory.exists()) {
			if (!directory.mkdirs()) {
				logger.error("Could not create " + chatEnum.name() + " directory");
				return null;
			}
		}

		if (!directory.isDirectory()) {
			logger.error("File " + chatEnum.name() + " is not a directory");
			return null;
		}

		File[] directoryFiles = directory.listFiles();
		if (directoryFiles == null) {
			logger.error("Path or I/O error, could not load files from directory " + directory);
			return null;
		}

		return directoryFiles;
	}
}

enum ChatEnum {
	reply, dialogue
}
