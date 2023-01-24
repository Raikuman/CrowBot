package com.raikuman.troubleclub.club.chat.configs;

import com.raikuman.botutilities.configs.ConfigInterface;

import java.util.LinkedHashMap;

/**
 * Provides configuration for chat
 *
 * @version 1.2 2023-23-01
 * @since 1.0
 */
public class ReplyConfig implements ConfigInterface {

	@Override
	public String fileName() {
		return "reply";
	}

	@Override
	public LinkedHashMap<String, String> getConfigs() {
		LinkedHashMap<String, String> configMap = new LinkedHashMap<>();
		configMap.put("commandchannelid", "");

		return configMap;
	}
}
