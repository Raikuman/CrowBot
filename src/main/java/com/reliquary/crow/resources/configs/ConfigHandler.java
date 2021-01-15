package com.reliquary.crow.resources.configs;

import com.reliquary.crow.resources.TextLineLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {

	private static final Logger logger = LoggerFactory.getLogger(ConfigHandler.class);

	/*
	loadConfigSetting
	Loads configs from config files
	 */
	public String loadConfigSetting(String fileName, String config) {

		// Check if file exists
		File file = new File(
			envConfig.get("configdirectory") + "/" + fileName + ".cfg"
		);

		String readConfig = null;
		if (file.exists()) {
			// Get config from file
			for (String arrayString : TextLineLoader.readFileToArray(file)) {
				if (arrayString.contains(config.toLowerCase())) {
					readConfig = arrayString;
					break;
				}
			}

			if (readConfig != null) {
				// Split config after =
				return readConfig.split("=")[1].toLowerCase();
			} else {
				// Return null, no command
				logger.info("Config setting " + config + " does not exist");
				return null;
			}
		} else {
			// Return null, no command
			logger.info("Config file " + file.getName() + " does not exist");
			return null;
		}
	}

	/*
	writeConfigSetting
	Write config setting to config file
	 */
	public void writeConfigSetting(String fileName, String configName, String configSetting) {

		// Check if file exists
		File file = new File(
			envConfig.get("configdirectory") + "/" + fileName + ".cfg"
		);

		if (file.exists()) {
			try {
				// Read file first, save array
				List<String> configArrayOriginal = TextLineLoader.readFileToArray(file);
				List<String> configArray = new ArrayList<>(configArrayOriginal);

				// Look for config setting
				for (String line : configArrayOriginal) {
					if (line.contains(configName)) {
						configArray.set(
							configArrayOriginal.indexOf(line),
							configName + "=" + configSetting
						);
						break;
					}
				}
				if (!configArray.equals(configArrayOriginal)) {
					// Write to file with BufferedWriter
					BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));

					for (String configRead : configArray) {
						writer.write(configRead);
						writer.newLine();
					}

					writer.close();
					logger.info("Config " + configName +
						" in file " + file.getName() +
						" was changed to " + configSetting);
				} else
					logger.info("Config file " + file.getName() + " was not changed");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
			logger.info("Config file " + file.getName() + " does not exist");
	}
}
