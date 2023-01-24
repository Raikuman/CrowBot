package com.raikuman.troubleclub.club.voice.configs;

import com.raikuman.botutilities.configs.ConfigInterface;

import java.util.LinkedHashMap;

/**
 * Provides configuration for voice
 *
 * @version 1.0 2023-23-01
 * @since 1.0
 */
public class VoiceConfig implements ConfigInterface {

	@Override
	public String fileName() {
		return "voice";
	}

	@Override
	public LinkedHashMap<String, String> getConfigs() {
		LinkedHashMap<String, String> configMap = new LinkedHashMap<>();
		configMap.put("guildid", "");
		configMap.put("voicechannelid", "");
		configMap.put("minSched", "");
		configMap.put("addMinSched", "");

		return configMap;
	}
}
