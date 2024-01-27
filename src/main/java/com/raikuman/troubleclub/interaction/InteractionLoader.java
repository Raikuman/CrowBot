package com.raikuman.troubleclub.interaction;

import com.raikuman.troubleclub.Club;
import com.raikuman.troubleclub.parser.Dialogue;
import com.raikuman.troubleclub.parser.DialogueParser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class InteractionLoader {

    private static final Logger logger = LoggerFactory.getLogger(InteractionLoader.class);

    public static List<InteractionCache> loadInteractionCaches() {
        // Directory for interactions
        File interactionDir = new File("resources" + File.separator + "interactions");
        try {
            if (interactionDir.createNewFile()) {
                logger.info("Created file {}", interactionDir.getAbsolutePath());
            } else {
                logger.info("File {} exists", interactionDir.getAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("Error creating file {}", interactionDir.getAbsolutePath());
        }

        // Get list of interaction files
        File[] files = interactionDir.listFiles();
        if (files == null) return new ArrayList<>();

        // Load cache for interactions
        List<InteractionCache> interactionCaches = new ArrayList<>();
        for (File file : files) {
            InteractionCache interactionCache = parseInteractionCache(file);
            if (interactionCache == null) continue;

            interactionCaches.add(interactionCache);
        }

        return interactionCaches;
    };

    private static InteractionCache parseInteractionCache(File file) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            boolean beginParse = false;
            List<Club> actors = new ArrayList<>();
            List<String> words = new ArrayList<>();
            int requiredMatches = 0;

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.equals("[Interaction]")) {
                    beginParse = true;
                    continue;
                }

                if (!beginParse) continue;

                // Retrieve interaction settings
                if (line.contains("actors")) {
                    // Parse all actors
                    String[] split = line.split("=");
                    if (split.length != 2) continue;

                    String[] actorSplit = split[1].split(",");
                    for (String actor : actorSplit) {
                        actors.add(parseClub(actor));
                    }
                } else if (line.contains("words")) {
                    // Parse all words
                    String[] split = line.split("=");
                    if (split.length != 2) continue;

                    String[] wordSplit = split[1].split(",");
                    words.addAll(Arrays.asList(wordSplit));
                } else if (line.contains("reqMatches")) {
                    String reqMatchesString = parseSettingString(line);
                    requiredMatches = parseIntegerFromString(reqMatchesString, 0);
                } else if (line.contains("[Settings]")) {
                    break;
                }
            }

            return new InteractionCache(actors, words, requiredMatches, file);
        } catch (IOException e) {
            logger.error("Error parsing interaction file {}", file.getName());
            return null;
        }
    }

    public static Dialogue loadInteractionDialogue(Message message, HashMap<Club, JDA> clubMap,
                                                                                   InteractionCache interactionCache) {
        return parseInteractionDialogue(
            interactionCache.interactionFile(),
            clubMap,
            message
        );
    }

    private static Dialogue parseInteractionDialogue(File file, HashMap<Club, JDA> clubMap, Message firstMessage) {
        List<Dialogue> dialogues = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("[Dialogue")) {
                    dialogues.add(
                        DialogueParser.parse(
                            bufferedReader,
                            line,
                            clubMap,
                            firstMessage
                        )
                    );
                }
            }
        } catch (IOException e) {
            logger.error("Error parsing interaction file {}", file.getName());
        }

        // Get random dialogue
        if (dialogues.isEmpty()) return null;

        int totalWeight = 0;
        for (Dialogue dialogue : dialogues) {
            totalWeight += dialogue.getChance();
        }

        Dialogue selectedDialogue = null;
        int random = (int) (Math.random() * totalWeight);
        int currentWeight = 0;
        for (Dialogue dialogue : dialogues) {
            currentWeight += dialogue.getChance();

            if (currentWeight >= random) {
                selectedDialogue = dialogue;
            }
        }

        return selectedDialogue;
    }

    private static String parseSettingString(String setting) {
        String[] split = setting.split("=");
        if (split.length != 2) return "";

        return split[1];
    }

    private static Club parseClub(String clubString) {
        try {
            return Club.valueOf(clubString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static int parseIntegerFromString(String chanceString, int defaultInteger) {
        try {
            return Integer.parseInt(chanceString);
        } catch (NumberFormatException e) {
            return defaultInteger;
        }
    }
}
