package com.raikuman.troubleclub.club.statemanager.configs;

import com.raikuman.botutilities.configs.ConfigInterface;

import java.util.LinkedHashMap;

/**
 * Provides configuration for managing states
 *
 * @version 1.0 2023-22-02
 * @since 1.0
 */
public class StateConfig implements ConfigInterface {

	@Override
	public String fileName() {
		return "state";
	}

	@Override
	public LinkedHashMap<String, String> getConfigs() {
		return new LinkedHashMap<>() {{
			put("disableStateCommands", "true");
			put("loggingchannelid", "");
			put("logstatus", "false");
			put("logreply", "false");
			put("logidle", "false");
			put("logmute", "false");
		}};
	}
}
