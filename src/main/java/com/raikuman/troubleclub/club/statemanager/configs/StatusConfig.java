package com.raikuman.troubleclub.club.statemanager.configs;

import com.raikuman.botutilities.configs.ConfigInterface;

import java.util.LinkedHashMap;

/**
 * Provides configuration for statuses
 *
 * @version 1.0 2023-22-02
 * @since 1.0
 */
public class StatusConfig implements ConfigInterface {
	@Override
	public String fileName() {
		return "troubleclub/status";
	}

	@Override
	public LinkedHashMap<String, String> getConfigs() {
		return new LinkedHashMap<>() {{
			put("lowerBoundMins", "15");
			put("addedUpperBoundMins", "15");
		}};
	}
}
