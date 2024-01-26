package com.raikuman.troubleclub.parser;

import com.raikuman.botutilities.config.ConfigData;
import com.raikuman.troubleclub.Club;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IllegalFormatException;

public class DialogueParser {

    private static final Logger logger = LoggerFactory.getLogger(DialogueParser.class);
    private static final ConfigData dialogueConfig = new ConfigData(new DialogueConfig());

    public static Dialogue parse(BufferedReader bufferedReader, String header, HashMap<Club, JDA> clubMap,
                                 String defaultChannel) throws IOException {
        // Parse header for chance
        int chance = 100;
        try {
            chance = Integer.parseInt(header.trim().substring(header.indexOf("=")).replace("]", "").trim());
        } catch (NumberFormatException e) {
            logger.error("Error parsing header for chance");
        }

        Dialogue dialogue = new Dialogue();
        dialogue.setChance(chance);

        // Get Discord target
        String guildString = new ConfigData(new DialogueConfig()).getConfig("targetguild");
        Guild targetGuild = clubMap.get(Club.SUU).getGuildById(guildString);

        if (targetGuild == null) {
            logger.error("Could not parse dialogue as target guild was null: {}", guildString);
            return null;
        }

        HashMap<Club, HashMap<String, GuildMessageChannelUnion>> channelMap = new HashMap<>();

        // Parse dialogue
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.isBlank()) break;

            String[] split = line.split("=");
            if (split.length < 2) continue;    // Empty/incorrect formatting

            // Check if actor is assigned to a specific channel
            Club actor;
            String targetChannel = defaultChannel;
            if (split[0].contains(",")) {
                String[] actorSplit = split[0].split(",");
                actor = parseActor(actorSplit[0]);

                if (actorSplit.length > 1) {
                    // Get specific target channel
                    targetChannel = actorSplit[1];
                }
            } else {
                actor = parseActor(split[0]);
            }

            // Get target channel
            GuildMessageChannelUnion channel = channelMap.get(actor).get(targetChannel);
            if (channel == null) {
                channel = parseChannel(actor, targetChannel, clubMap);

                // Cache channel
                channelMap.putIfAbsent(actor, new HashMap<>());
            }

            // Dialogue has incorrect formatting, exit
            if (actor == null || channel == null) {
                logger.error("Error parsing actor or channel: {}", split[0]);
                return null;
            }

            String[] lineSplit = split[1].split("\\s+");

            // Check current split for components
            String currentSplit = lineSplit[0];
            Components components = new Components();
            components.setSticker(parseSticker(currentSplit, targetGuild));
            components.setReaction(parseEmoji(currentSplit));

            // Check for another component in next split
            if (lineSplit.length > 2) {
                components.setSticker(parseSticker(currentSplit, targetGuild));
                components.setReaction(parseEmoji(currentSplit));
            }

            String sentence = String.join(" ", Arrays.copyOfRange(lineSplit, components.getComponentCount(),
                lineSplit.length));

            dialogue.addLine(
                actor,
                channel,
                sentence,
                components.getSticker(),
                components.getReaction()
            );
        }

        return dialogue;
    }

    private static Club parseActor(String actor) {
        try {
            return Club.valueOf(actor.trim().toUpperCase());
        } catch (IllegalFormatException e) {
            logger.error("Error parsing actor: {}", actor);
            return null;
        }
    }

    private static GuildMessageChannelUnion parseChannel(Club actor, String channel, HashMap<Club, JDA> clubMap) {
        JDA jda = clubMap.get(actor);

        Guild guild = jda.getGuildById(dialogueConfig.getConfig("targetguild"));
        if (guild == null) {
            logger.error("Could not parse dialogue as target guild was null: {}", dialogueConfig.getConfig("targetguild"));
            return null;
        }

        return guild.getChannelById(GuildMessageChannelUnion.class, channel);
    }

    private static GuildSticker parseSticker(String sticker, Guild guild) {
        if (sticker.contains("((") && sticker.contains("))")) return null;

        String stickerName = sticker.trim().replace("((", "").replace("))", "");
        return guild.getStickerById(stickerName + "sticker");
    }

    private static Emoji parseEmoji(String emoji) {
        if (emoji.contains("(<") && emoji.contains(">)")) return null;

        String emojiCode = emoji.trim().replace("(<", "").replace(">)", "");

        try {
            return Emoji.fromFormatted(emojiCode);
        } catch (IllegalFormatException e) {
            logger.error("Error parsing emoji from: {}", emoji);
            return null;
        }
    }

    private static class Components {

        private GuildSticker sticker = null;
        private Emoji reaction = null;

        public GuildSticker getSticker() {
            return sticker;
        }

        public void setSticker(GuildSticker sticker) {
            this.sticker = sticker;
        }

        public Emoji getReaction() {
            return reaction;
        }

        public void setReaction(Emoji reaction) {
            this.reaction = reaction;
        }

        public int getComponentCount() {
            int components = 0;

            if (sticker != null) components++;
            if (reaction != null) components++;

            return components;
        }
    }
}
