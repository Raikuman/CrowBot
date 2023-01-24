package com.raikuman.troubleclub.club.chat.configs;

import com.raikuman.botutilities.configs.ConfigInterface;

import java.util.LinkedHashMap;

/**
 * Provides configuration for dialogue scheduling
 *
 * @version 1.1 2023-23-01
 * @since 1.0
 */
public class DialogueSchedulerConfig implements ConfigInterface {

	@Override
	public String fileName() {
		return "dialoguescheduler";
	}

	@Override
	public LinkedHashMap<String, String> getConfigs() {
		LinkedHashMap<String, String> configMap = new LinkedHashMap<>();
		configMap.put("dialoguechannelid", "");
		configMap.put("minScheduledDays", "1");
		configMap.put("bypassRandomAddedDays", "false");
		configMap.put("crowsticker", "");
		configMap.put("dessticker", "");
		configMap.put("suusticker", "");
		configMap.put("inoristicker", "");
		configMap.put("selectdate", "1999-01-01");

		return configMap;
	}
}
