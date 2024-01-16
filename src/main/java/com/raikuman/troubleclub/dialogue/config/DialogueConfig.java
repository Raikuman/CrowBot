package com.raikuman.troubleclub.dialogue.config;

import com.raikuman.botutilities.config.Config;

import java.util.LinkedHashMap;

public class DialogueConfig implements Config {

    @Override
    public String fileName() {
        return "dialogue/dialogue";
    }

    @Override
    public LinkedHashMap<String, String> configs() {
        LinkedHashMap<String, String> configMap = new LinkedHashMap<>();
        configMap.put("targetguild", "");
        configMap.put("targetchannel", "");
        configMap.put("weeksbetween", "1");
        configMap.put("daily", "false");
        configMap.put("wpm", "40");
        configMap.put("suusticker", "");
        configMap.put("crowsticker", "");
        configMap.put("dessticker", "");
        configMap.put("inoristicker", "");

        return configMap;
    }
}
