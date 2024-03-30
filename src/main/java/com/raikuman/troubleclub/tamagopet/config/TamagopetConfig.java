package com.raikuman.troubleclub.tamagopet.config;

import com.raikuman.botutilities.config.Config;

import java.util.LinkedHashMap;

public class TamagopetConfig implements Config {

    @Override
    public String fileName() {
        return "tamagopet";
    }

    @Override
    public LinkedHashMap<String, String> configs() {
        LinkedHashMap<String, String> configMap = new LinkedHashMap<>();
        configMap.put("benchannel", "");
        configMap.put("categoryemojiid", "");
        configMap.put("pointstext", "1");
        configMap.put("pointsimage", "20");
        configMap.put("maxhappiness", "100");

        return configMap;
    }
}
