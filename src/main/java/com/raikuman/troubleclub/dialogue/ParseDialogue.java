package com.raikuman.troubleclub.dialogue;

import com.raikuman.botutilities.config.ConfigData;
import com.raikuman.troubleclub.Club;
import com.raikuman.troubleclub.dialogue.config.DialogueConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class ParseDialogue {

    private static final Logger logger = LoggerFactory.getLogger(ParseDialogue.class);

    public static Dialogue parseDialogue(HashMap<Club, JDA> clubMap, File file) {
        Dialogue dialogue = new Dialogue();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            ConfigData configData = new ConfigData(new DialogueConfig());
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // Read dialogue
                String[] split = line.split(":", 2);
                if (split.length != 2) {
                    continue;
                }

                // Get dialogue speed
                double dialogueSpeed = 1;
                if (split[0].contains(",")) {
                    String[] speedSplit = split[0].split(",");

                    // Clean up split for actor
                    split[0] = speedSplit[0];

                    // Handle getting speed
                    if (speedSplit.length == 2) {
                        try {
                            dialogueSpeed = Double.parseDouble(speedSplit[1]);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }

                // Get actor
                Club actor;
                try {
                    actor = Club.valueOf(split[0].toUpperCase());
                } catch (IllegalArgumentException e) {
                    logger.error("Invalid actor name: " + split[0]);
                    continue;
                }

                // Add actor to dialogue
                if (!dialogue.checkForActor(actor)) {
                    JDA jda = clubMap.get(actor);
                    if (jda == null) {
                        continue;
                    }

                    dialogue.addActor(actor, jda);
                }

                // Parse dialogue
                String text = split[1].trim();

                // Try for sticker
                GuildSticker sticker = null;
                if (text.split("\\s+").length == 1 && text.contains("((") && text.contains("))")) {
                    try {
                        sticker = Objects.requireNonNull(clubMap.get(Club.SUU)
                                .getGuildById(configData.getConfig("targetguild")))
                            .getStickerById(configData.getConfig(actor.name().toLowerCase() + "sticker"));
                    } catch (Exception e) {
                        continue;
                    }
                }

                dialogue.addLine(actor, text, sticker, dialogueSpeed);
            }
        } catch (IOException e) {
            logger.error("Failed to parse dialogue file: " + file.getName());
            return null;
        }

        return dialogue;
    }
}
