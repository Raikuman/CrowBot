package com.raikuman.troubleclub.club.chat.configs;

import com.raikuman.botutilities.configs.ConfigInterface;

import java.util.LinkedHashMap;

/**
 * Provides configuration for chat
 *
 * @version 1.1 2023-20-01
 * @since 1.0
 */
public class ChatConfig implements ConfigInterface {

	@Override
	public String fileName() {
		return "chat";
	}

	@Override
	public LinkedHashMap<String, String> getConfigs() {
		LinkedHashMap<String, String> configMap = new LinkedHashMap<>();
		configMap.put("replycommandchannelid", "");
		configMap.put("dialoguechannelid", "");
		configMap.put("crowsticker", "");
		configMap.put("dessticker", "");
		configMap.put("suusticker", "");
		configMap.put("inoristicker", "");

		return configMap;
	}
}
