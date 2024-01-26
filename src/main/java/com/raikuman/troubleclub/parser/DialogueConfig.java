package com.raikuman.troubleclub.parser;

import com.raikuman.botutilities.config.Config;

import java.util.LinkedHashMap;

public class DialogueConfig implements Config {

    @Override
    public String fileName() {
        return "dialogue";
    }

    @Override
    public LinkedHashMap<String, String> configs() {
        LinkedHashMap<String, String> configMap = new LinkedHashMap<>();
        configMap.put("targetguild", "");
        configMap.put("suugoisticker", "");
        configMap.put("watsticker", "");
        configMap.put("clapsticker", "");
        configMap.put("<3sticker", "");

        return configMap;
    }
}
