package com.reliquary.crow.resources.configs;

import com.reliquary.crow.resources.other.FileLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles loading and editing config files and settings
 *
 * @version 1.1 2021-09-11
 * @since 1.0
 */
public class ConfigHandler {

	private static final Logger logger = LoggerFactory.getLogger(ConfigHandler.class);

	/**
	 * This method gets a config setting from the given file and string by searching the file
	 * @param fileName The name of the file to open
	 * @param config The config setting to search for
	 * @return Returns the setting of the config
	 */
	public static String loadConfigSetting(String fileName, String config) {

		File file = new File(envConfig.get("configdirectory") + "/" + fileName + ".cfg");

		// Check if file exists
		if (!file.exists()) {
			logger.info("Config file " + file.getName() + " does not exist");
			return null;
		}

		// Get config from file
		String readConfig = null;
		for (String arrayString : FileLoader.readFileToArray(file))
			if (arrayString.toLowerCase().contains(config.toLowerCase())) {
				readConfig = arrayString;
				break;
			}

		if (readConfig != null) {

			// Get setting of the config
			return readConfig.split("=")[1].toLowerCase();
		}
		else {
			logger.info("Config setting " + config + " not found");
			return null;
		}
	}

	/**
	 * This method opens a file and searches for the config name to replace it with the corresponding setting
	 * @param fileName The name of the file to open
	 * @param configName The config name to search for
	 * @param configSetting The config setting to edit the config
	 */
	public static void writeConfigSetting(String fileName, String configName, String configSetting) {

		File file = new File(envConfig.get("configdirectory") + "/" + fileName + ".cfg");

		// Check if file exists
		if (!file.exists()) {
			logger.info("Config file " + file.getName() + " does not exist");
			return;
		}

		// Read file and save settings into an array
		List<String> originalConfig = FileLoader.readFileToArray(file);
		List<String> outputConfig = new ArrayList<>(originalConfig);

		// Search for config setting to change
		boolean configChanged = false;
		for (String searchConfig : outputConfig) {
			if (searchConfig.toLowerCase().contains(configName.toLowerCase())) {
				outputConfig.set(
					outputConfig.indexOf(searchConfig),
					configName + "=" + configSetting
				);
				configChanged = true;
				break;
			}
		}

		if (configChanged) {
			try {

				// Write to file
				BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));

				for (String configRead : outputConfig) {
					writer.write(configRead);
					writer.newLine();
				}

				writer.close();

				logger.info("Config " + configName +
					" in file " + file.getName() +
					" was changed to " + configSetting);

			} catch (IOException e) {
				logger.info("An error writing config " + configName + " to " + configSetting + " in file " +
					file.getName() + " produced an error");
				e.printStackTrace();
			}
		} else
			logger.info("Config file " + file.getName() + " was not changed");
	}
}
