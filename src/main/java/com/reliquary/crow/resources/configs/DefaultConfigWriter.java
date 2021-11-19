package com.reliquary.crow.resources.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class handles writing all the needed default configs for the functionality of the bot
 *
 * @version 1.0.1
 * @since 2021-09-11
 */
public class DefaultConfigWriter {

	private final Logger logger = LoggerFactory.getLogger(DefaultConfigWriter.class);

	/**
	 * Writes the default config for bot settings
	 */
	public void writeBotSettingsConfigFile() {

		List<String> configSettings = new ArrayList<>(Arrays.asList(
			"prefix=!",
			"presencedirectory=presence",
			"commandresourcedirectory=commandresources"
		));

		String directory = "config";
		String fileName = "botSettings";

		File file = new File(directory + "/" + fileName + ".cfg");

		// Check if file exists
		if (checkConfigExists(file))
			return;

		// Write config file
		writeConfigFile(configSettings, createConfigFile(directory + "/" + fileName));
	}

	/**
	 * Writes the default config for the YamBoard
	 */
	public void writeYamBoardConfigFile() {
		List<String> configSettings = new ArrayList<>(Arrays.asList(
			"yamboardEmoji=U+1f360",
			"chatTextChannel=",
			"yamPostChannel="
		));

		String directory = "config";
		String fileName = "yamboardSettings";

		File file = new File(directory + "/" + fileName + ".cfg");

		// Check if file exists
		if (checkConfigExists(file))
			return;

		// Write config file
		writeConfigFile(configSettings, createConfigFile(directory + "/" + fileName));

	}

	/**
	 * This method writes into the given config file all the default config names
	 * @param config Config names to write in the config file
	 * @param file File to write config names into
	 */
	private void writeConfigFile(List<String> config, File file) {

		try {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));

		for (String configSetting : config) {
			writer.write(configSetting);
			writer.newLine();
		}

		writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method creates a file and provides some checks and logging
	 * @param fileName Provides the name of the file when creating a new file
	 * @return Returns the newly created file
	 */
	private File createConfigFile(String fileName) {

		File configFile = new File(fileName + ".cfg");

		try {
			if (configFile.createNewFile())
				logger.info("Created config " + configFile.getName() + " successfully");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return configFile;
	}

	/**
	 * This method checks if a file exists and provides logging
	 * @param file The file to check if it exists
	 * @return Returns a boolean value whether the file exists
	 */
	private boolean checkConfigExists(File file) {
		if (!file.exists())
			return false;
		else {
			logger.info("Config " + file.getName() + " already created. Continuing...");
			return true;
		}
	}
}
