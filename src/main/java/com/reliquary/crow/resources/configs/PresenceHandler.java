package com.reliquary.crow.resources.configs;

import com.reliquary.crow.resources.other.FileLoader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * This class handles updating the presence activity of the bot
 *
 * @version 1.0.1
 * @since 2021-19-11
 */
public class PresenceHandler {

	private static final Logger logger = LoggerFactory.getLogger(PresenceHandler.class);

	/*
	writePresenceFiles
	Creates files for presence
	 */
	public static void writePresenceFiles() {

		// Get directory
		String presenceDirectory = envConfig.get("configdirectory") +
			"/" + ConfigHandler.loadConfigSetting("botSettings", "presencedirectory") + "/";

		// Get files
		File playingFile = new File(presenceDirectory + "playing.txt");
		File watchingFile = new File(presenceDirectory + "watching.txt");
		File listeningFile = new File(presenceDirectory + "listening.txt");

		// Create files
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

		// Get directory
		String presenceDirectory = envConfig.get("configdirectory") +
			"/" + ConfigHandler.loadConfigSetting("botSettings", "presencedirectory") + "/";

		// Get files
		File playingFile = new File(presenceDirectory + "playing.txt");
		File watchingFile = new File(presenceDirectory + "watching.txt");
		File listeningFile = new File(presenceDirectory + "listening.txt");

		// Select a random presence
		Random rand = new Random();
		int initialNum = rand.nextInt(3);

		switch(initialNum) {

			case 0:
				jda.getPresence().setPresence(
					OnlineStatus.ONLINE,
					Activity.playing(getPresence(playingFile, rand))
				);
				break;

			case 1:
				jda.getPresence().setPresence(
					OnlineStatus.ONLINE,
					Activity.watching(getPresence(watchingFile, rand))
				);
				break;

			case 2:
				jda.getPresence().setPresence(
					OnlineStatus.ONLINE,
					Activity.listening(getPresence(listeningFile, rand))
				);
				break;
		}
	}

	private static String getPresence(File file, Random rand) {

		List<String> presenceArray = FileLoader.readFileToArray(file);

		if (!presenceArray.isEmpty())
			return presenceArray.get(rand.nextInt(presenceArray.size()));
		else
			return "No presence found!";
	}
}
