package com.raikuman.troubleclub.conversation.config;

import com.raikuman.botutilities.config.Config;

import java.util.LinkedHashMap;

public class DayWeights implements Config {

    @Override
    public String fileName() {
        return "conversation/dayweights";
    }

    @Override
    public LinkedHashMap<String, String> configs() {
        LinkedHashMap<String, String> configMap = new LinkedHashMap<>();
        configMap.put("1", "0");
        configMap.put("2", "0");
        configMap.put("3", "0");
        configMap.put("4", "0");
        configMap.put("5", "0");
        configMap.put("6", "0");
        configMap.put("7", "0");
        return configMap;
    }
}
