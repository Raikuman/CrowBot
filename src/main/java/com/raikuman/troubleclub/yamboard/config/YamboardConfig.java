package com.raikuman.troubleclub.yamboard.config;

import com.raikuman.botutilities.config.Config;

import java.util.LinkedHashMap;

public class YamboardConfig implements Config {

    @Override
    public String fileName() {
        return "yamboard";
    }

    @Override
    public LinkedHashMap<String, String> configs() {
        LinkedHashMap<String, String> configMap = new LinkedHashMap<>();
        configMap.put("reactionemoji", "\uD83C\uDF60");
        configMap.put("upemoji", "\uD83D\uDD3C");
        configMap.put("downemoji", "\uD83D\uDD3D");
        configMap.put("enableselfkarma", "false");
        configMap.put("yamchannel", "");
        configMap.put("listenchannel", "");

        return configMap;
    }
}
