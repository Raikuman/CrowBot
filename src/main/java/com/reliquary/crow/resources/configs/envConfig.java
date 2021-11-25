package com.reliquary.crow.resources.configs;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * This class loads information from the .env file
 *
 * @version 1.1 2021-19-11
 * @since 2021-19-11
 */
public class envConfig {

	private static final Dotenv dotenv = Dotenv.load();

	/**
	 * This method returns the config setting of the given key
	 * @param key Provides the string to look for the config
	 * @return Returns the config setting
	 */
	public static String get(String key) {
		return dotenv.get(key.toUpperCase());
	}
}
