package com.raikuman.troubleclub.interaction;

import com.raikuman.botutilities.config.ConfigData;
import com.raikuman.troubleclub.Club;
import com.raikuman.troubleclub.dialogue.config.DialogueConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
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
                } else if (line.contains("[Dialogue")) {
                    break;
                }
            }

            return new InteractionCache(actors, words, requiredMatches, file);
        } catch (IOException e) {
            logger.error("Error parsing interaction file {}", file.getName());
            return null;
        }
    }

    public static Interaction loadInteraction(Message message, HashMap<Club, JDA> clubMap,
                                               InteractionCache interactionCache) {
        // Get interaction dialogue
        Dialogue dialogue = parseInteractionFile(interactionCache.interactionFile());
        if (dialogue == null) return null;

        // Retrieve actors
        HashMap<Club, GuildMessageChannelUnion> actors = new HashMap<>();
        for (Club club : interactionCache.actors()) {
            actors.put(
                club,
                clubMap.get(club).getChannelById(GuildMessageChannelUnion.class, message.getChannelId()));
        }

        Interaction interaction = new Interaction(actors, dialogue.isCommand(), dialogue.invoker(),
                dialogue.command());

        // Parse interaction dialogue
        for (String line : dialogue.lines()) {
            // Parse line
            Interaction.Line interactionLine = parseLine(line, actors);
            if (interactionLine == null) return null;

            interaction.addLine(interactionLine);
        }

        return interaction;
    }

    private static Dialogue parseInteractionFile(File file) {
        List<Dialogue> dialogues = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            boolean beginParse = false, isCommand = false;
            Club invoker = null;
            String command = "";
            int chance = 100;

            List<String> dialogueLines = new ArrayList<>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // Wait to parse interaction
                if (line.equals("[Settings]")) {
                    beginParse = true;
                    continue;
                }

                if (!beginParse) continue;

                // Retrieve interaction settings
                if (line.contains("isCommand")) {
                    String[] split = line.split("=");
                    if (split.length == 2) {
                        isCommand= Boolean.parseBoolean(split[1]);
                    }
                } else if (line.contains("invoker")) {
                    invoker = parseClub(parseSettingString(line));

                } else if (line.contains("command")) {
                    command = parseSettingString(line);
                }

                // Retrieve lines
                if (line.contains("[Dialogue")) {
                    // Check for chance
                    if (line.contains("=")) {
                        String chanceString = parseSettingString(line).replace("]", "");
                        chance = parseIntegerFromString(chanceString, 100);
                    } else {
                        chance = 100;
                    }

                    // Add lines to dialogue
                    dialogueLines.add(line);

                    // Construct dialogue
                    if (line.isBlank()) {
                        dialogues.add(new Dialogue(chance, isCommand, invoker, command, dialogueLines));
                        dialogueLines = new ArrayList<>();
                    }
                }
            }

            // Ensure last dialogue is added to list
            if (!dialogueLines.isEmpty()) {
                dialogues.add(new Dialogue(chance, isCommand, invoker, command, dialogueLines));
            }
        } catch (IOException e) {
            logger.error("Failed to read interaction file: {}", file.getName());
        }

        // Get random dialogue
        if (dialogues.isEmpty()) return null;

        int totalWeight = 0;
        for (Dialogue dialogue : dialogues) {
            totalWeight += dialogue.chance();
        }

        Dialogue selectedDialogue = null;
        int random = (int) (Math.random() * totalWeight);
        int currentWeight = 0;
        for (Dialogue dialogue : dialogues) {
            currentWeight += dialogue.chance();

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

    private static Interaction.Line parseLine(String line, HashMap<Club, GuildMessageChannelUnion> actors) {
        String[] split = line.split("=");
        if (split.length != 2) return null;

        // Get actor
        Club actor;
        try {
            actor = Club.valueOf(split[0]);
        } catch (IllegalArgumentException e) {
            logger.error("Could not parse actor for line: {}", line);
            return null;
        }

        // Check for sticker or reaction
        List<String> messageSplit = new ArrayList<>(Arrays.stream(split[1].split(" ")).toList());
        if (messageSplit.isEmpty()) return null;

        // Check for reaction or sticker
        boolean isReaction = false, isSticker = false;
        String reactionOrSticker = messageSplit.get(0);
        if (reactionOrSticker.contains("(<") && reactionOrSticker.contains(">)")) {
            // Is reaction
            isReaction = true;
        } else if (reactionOrSticker.contains("((") && reactionOrSticker.contains("))")) {
            // Is sticker
            isSticker = true;
        }

        // Get reaction
        Emoji reaction = null;
        if (isReaction) {
            String stringReaction = messageSplit.get(0).replace("(<", "").replace(">)", "");
            reaction = Emoji.fromFormatted(stringReaction);
        }

        // Get sticker
        GuildSticker sticker = null;
        ConfigData configData = new ConfigData(new DialogueConfig());
        if (isSticker) {
            try {
                sticker = Objects.requireNonNull(
                    actors.get(actor).getJDA()
                        .getGuildById(configData.getConfig("targetguild")))
                        .getStickerById(configData.getConfig(actor.name().toLowerCase() + "sticker"));
            } catch (Exception e) {
                logger.error("Could not parse sticker with actor: {}", actor);
            }
        }

        // Remove first split
        messageSplit.remove(0);

        if (messageSplit.isEmpty()) {
            return new Interaction.Line(
                actors.get(actor),
                reaction,
                sticker,
                "");
        } else {
            return new Interaction.Line(
                actors.get(actor),
                reaction,
                sticker,
                String.join(" ", messageSplit));
        }
    }

    record Dialogue(int chance, boolean isCommand, Club invoker, String command, List<String> lines) {}
}
