package com.raikuman.troubleclub.club.statemanager.configs;

import com.raikuman.botutilities.configs.ConfigInterface;

import java.util.LinkedHashMap;

/**
 * Provides configuration for dialogue
 *
 * @version 1.0 2023-19-02
 * @since 1.0
 */
public class DialogueConfig implements ConfigInterface {

	@Override
	public String fileName() {
		return "troubleclub/dialogue";
	}

	@Override
	public LinkedHashMap<String, String> getConfigs() {
		return new LinkedHashMap<>() {{
			put("disableDialogue", "false");
			put("textchannelid", "");
			put("crowsticker", "");
			put("dessticker", "");
			put("suusticker", "");
			put("inoristicker", "");
			put("minimumScheduledDays", "5");
			put("maxRNGDays", "3");
			put("bypassRNGDays", "false");
			put("overwritepreviousdialogue", "false");
			put("selectdate", "1999-01-01");
		}};
	}
}
