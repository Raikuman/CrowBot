package com.reliquary.crow.resources.configs;

import io.github.cdimascio.dotenv.Dotenv;

public class envConfig {

	// Load configuration from .env
	private static final Dotenv dotenv = Dotenv.load();

	/*
	get
	Returns config from env file using a key
	 */
	public static String get(String key) {
		return dotenv.get(key.toUpperCase());
	}
}
