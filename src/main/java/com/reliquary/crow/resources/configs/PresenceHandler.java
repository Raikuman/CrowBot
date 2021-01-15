package com.reliquary.crow.resources.configs;

import com.reliquary.crow.resources.TextLineLoader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PresenceHandler {

	private static final Logger logger = LoggerFactory.getLogger(PresenceHandler.class);

	/*
	writePresenceFiles
	Open new thread to change presence on a timer
	 */
	public static void writePresenceFiles() {

		// Check for presence files
		String presenceDirectory = envConfig.get("configdirectory") +
			"/" + envConfig.get("presencedirectory") + "/";

		File playingFile = new File(presenceDirectory + "playing.txt");
		File watchingFile = new File(presenceDirectory + "watching.txt");
		File listeningFile = new File(presenceDirectory + "listening.txt");

		try {
			if (!playingFile.exists() && playingFile.createNewFile())
				logger.info("Created presence file " + playingFile.getName() + " successfully");

			if (!watchingFile.exists() && watchingFile.createNewFile())
				logger.info("Created presence file " + watchingFile.getName() + " successfully");

			if (!listeningFile.exists() && listeningFile.createNewFile())
				logger.info("Created presence file " + listeningFile.getName() + " successfully");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	updatePresence
	Update JDA object with a new presence
	 */
	public static void updatePresence(JDA jda) {

		// Load presence array from files
		String presenceDirectory = envConfig.get("configdirectory") +
			"/" + envConfig.get("presencedirectory") + "/";

		File playingFile = new File(presenceDirectory + "playing.txt");
		File watchingFile = new File(presenceDirectory + "watching.txt");
		File listeningFile = new File(presenceDirectory + "listening.txt");
		List<List<String>> presenceArray = loadFromPresence(playingFile, watchingFile, listeningFile);

		// Select a random presence
		Random rand = new Random();
		int initialNum = rand.nextInt(3);

		// Check if the specific presence array is empty
		String presence = "";
		if (!presenceArray.get(initialNum).isEmpty()) {
			presence = presenceArray
				.get(initialNum)
				.get(rand.nextInt(presenceArray.get(initialNum).size()));
		}

		// Set presence
		switch (initialNum) {
			case 0:
				if (!presenceArray.get(0).isEmpty())
					jda.getPresence().setPresence(
						OnlineStatus.ONLINE,
						Activity.playing(presence)
					);
				break;

			case 1:
				if (!presenceArray.get(1).isEmpty())
					jda.getPresence().setPresence(
						OnlineStatus.ONLINE,
						Activity.watching(presence)
					);
				break;

			case 2:
				if (!presenceArray.get(2).isEmpty())
					jda.getPresence().setPresence(
						OnlineStatus.ONLINE,
						Activity.listening(presence)
					);
				break;
		}
	}

	/*
	loadFromPresence
	Load a 2D array with arrays of presence strings
	 */
	private static List<List<String>> loadFromPresence(File file1, File file2, File file3) {

		// Load presence text from files
		List<List<String>> presenceArray = new ArrayList<>(3);

		presenceArray.add(TextLineLoader.readFileToArray(file1));
		presenceArray.add(TextLineLoader.readFileToArray(file2));
		presenceArray.add(TextLineLoader.readFileToArray(file3));

		return presenceArray;
	}
}
