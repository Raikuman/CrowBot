package com.raikuman.troubleclub.conversation.config;

import com.raikuman.botutilities.config.Config;

import java.util.LinkedHashMap;

public class HourWeights implements Config {

    @Override
    public String fileName() {
        return "conversation/hourweights";
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
        configMap.put("8", "0");
        configMap.put("9", "0");
        configMap.put("10", "0");
        configMap.put("11", "0");
        configMap.put("12", "0");
        configMap.put("13", "0");
        configMap.put("14", "0");
        configMap.put("15", "0");
        configMap.put("16", "0");
        configMap.put("17", "0");
        configMap.put("18", "0");
        configMap.put("19", "0");
        configMap.put("20", "0");
        configMap.put("21", "0");
        configMap.put("22", "0");
        configMap.put("23", "0");
        return configMap;
    }
}
