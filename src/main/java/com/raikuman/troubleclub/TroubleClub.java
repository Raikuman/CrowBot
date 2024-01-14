package com.raikuman.troubleclub;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

public class TroubleClub {

    private static final Logger logger = LoggerFactory.getLogger(TroubleClub.class);

    public static void main(String[] args) {
        HashMap<Club, JDA> clubMap = new HashMap<>();
        for (Club club : Club.values()) {
            try {
                clubMap.put(
                    club,
                    JDABuilder
                        .createDefault(System.getenv(club + "TOKEN"))
                        .enableIntents(List.of(
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.MESSAGE_CONTENT))
                        .setChunkingFilter(ChunkingFilter.ALL)
                        .enableCache(CacheFlag.VOICE_STATE)
                        //.addEventListeners()
                        .setMemberCachePolicy(MemberCachePolicy.ALL)
                        .setMaxReconnectDelay(32)
                        .setAutoReconnect(true)
                        .setRequestTimeoutRetry(true)
                        .build()
                );
            } catch (IllegalArgumentException e) {
                logger.error("Failed to initialize " + club + " bot: " + e.getMessage());
            }
        }


    }
}
