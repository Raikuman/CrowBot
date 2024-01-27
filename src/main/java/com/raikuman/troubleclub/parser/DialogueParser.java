package com.raikuman.troubleclub.parser;

import com.raikuman.botutilities.config.ConfigData;
import com.raikuman.troubleclub.Club;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
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
                                 Message firstMessage) throws IOException {
        // Parse header for chance
        int chance = 100;
        try {
            chance = Integer.parseInt(header.trim().substring(header.indexOf("=")).replace("]", "").trim());
        } catch (NumberFormatException e) {
            logger.error("Error parsing header for chance");
        }

        Dialogue dialogue = new Dialogue();
        dialogue.setPreviousMessage(firstMessage);
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

            // Handle parsing actor and line settings
            String actorSettings = split[0];

            // Get actor
            Club actor = parseActor(actorSettings.substring(0, actorSettings.indexOf("(")));

            // Handle line settings
            String targetChannel = firstMessage.getChannelId();
            double lineSpeed = 1.0;
            if (actorSettings.contains("(") && actorSettings.contains(")")) {
                actorSettings = actorSettings.substring(actorSettings.indexOf("(") + 1, actorSettings.indexOf(")") - 1);

                // Get specific target channel
                String[] settings = actorSettings.split(",");
                for (String setting : settings) {
                    if (setting.contains("channel")) {
                        targetChannel = setting.substring(setting.indexOf("=") + 1);
                    } else if (setting.contains("speed")) {
                        try {
                            lineSpeed = Double.parseDouble(setting.substring(setting.indexOf("=") + 1));
                        } catch (NumberFormatException e) {
                            logger.error("Error parsing line speed: {}", setting);
                        }
                    }
                }
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

            // Check current split for stickers/emojis
            String currentSplit = lineSplit[0];
            LineComponents lineSettings = new LineComponents();
            lineSettings.setSticker(parseSticker(currentSplit, targetGuild));
            lineSettings.setReaction(parseEmoji(currentSplit));

            // Check for another sticker/emoji in next split
            if (lineSplit.length > 2) {
                lineSettings.setSticker(parseSticker(currentSplit, targetGuild));
                lineSettings.setReaction(parseEmoji(currentSplit));
            }

            String sentence = String.join(" ", Arrays.copyOfRange(lineSplit, lineSettings.getComponentCount(),
                lineSplit.length));

            dialogue.addLine(
                actor,
                channel,
                sentence,
                lineSpeed,
                lineSettings.getSticker(),
                lineSettings.getReaction()
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

    private static class LineComponents {

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
