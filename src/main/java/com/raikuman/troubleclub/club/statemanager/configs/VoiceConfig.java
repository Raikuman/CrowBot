package com.raikuman.troubleclub.club.statemanager.configs;

import com.raikuman.botutilities.configs.ConfigInterface;

import java.util.LinkedHashMap;

/**
 * Provides configuration for voice connections
 *
 * @version 1.0 2023-22-02
 * @since 1.0
 */
public class VoiceConfig implements ConfigInterface {

	@Override
	public String fileName() {
		return "troubleclub/voice";
	}

	@Override
	public LinkedHashMap<String, String> getConfigs() {
		return new LinkedHashMap<>() {{
			put("guildid", "");
			put("voicechannelid", "");
			put("lowerBoundMins", "15");
			put("addedUpperBoundMins", "15");
			put("idlingLowerBoundMins", "4");
			put("idlingAddedUpperBoundMins", "10");
		}};
	}
}
