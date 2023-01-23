package com.raikuman.troubleclub.club.chat.configs;

import com.raikuman.botutilities.configs.ConfigInterface;

import java.util.LinkedHashMap;

public class DialogueSchedulerConfig implements ConfigInterface {

	@Override
	public String fileName() {
		return "dialoguescheduler";
	}

	@Override
	public LinkedHashMap<String, String> getConfigs() {
		LinkedHashMap<String, String> configMap = new LinkedHashMap<>();
		configMap.put("selectdate", "1999-01-01");

		return configMap;
	}
}
