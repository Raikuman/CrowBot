package com.raikuman.troubleclub.interaction;

import com.raikuman.botutilities.config.ConfigData;
import com.raikuman.troubleclub.Club;
import com.raikuman.troubleclub.ClubNicknames;
import com.raikuman.troubleclub.dialogue.Dialogue;
import com.raikuman.troubleclub.dialogue.DialogueConfig;
import com.raikuman.troubleclub.dialogue.DialogueParser;
import com.raikuman.troubleclub.dialogue.DialoguePlayer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class InteractionManager {

    private static final Logger logger = LoggerFactory.getLogger(InteractionManager.class);
    private final List<InteractionCache> interactionCaches;
    private HashMap<Club, JDA> clubMap;

    public InteractionManager() {
        this.interactionCaches = new ArrayList<>();
        loadInteractionCaches();
    }

    private void loadInteractionCaches() {
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
        if (files == null) return;

        // Load cache for interactions
        for (File file : files) {
            Interaction interaction = DialogueParser.getInteraction(file.toPath());
            if (interaction == null) continue;

            // Get all actors in the interaction
            List<Club> actors = new ArrayList<>();
            for (Dialogue dialogue : interaction.getDialogues()) {
                for (Dialogue.Line line : dialogue.getLines()) {
                    if (actors.contains(line.getActor())) continue;
                    actors.add(line.getActor());
                }
            }

            interactionCaches.add(new InteractionCache(
                actors,
                interaction.getWords(),
                interaction.getReqWords(),
                file
            ));
        }
    }

    public void setClubMap(HashMap<Club, JDA> clubMap) {
        this.clubMap = clubMap;
    }

    public void handleEvent(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        // Construct list of keys for retrieving interaction
        List<String> keys = new ArrayList<>(List.of(event.getMessage().getContentRaw().trim().split(" ")));

        // Get all caches that meet requirements
        InteractionData interactionData = extractInteractionData(event.getMessage().getMentions().getMembers(), keys);
        if (interactionData.caches().isEmpty()) {
            // Select random actor in data and send reply message for no interaction
            replyUnknownInteraction(interactionData.actors(), event.getMessage());
            logger.info("No interaction found, sending unknown interaction message.");
            return;
        }

        // Select a random interaction from the data
        Random rand = new Random();
        Interaction interaction = DialogueParser.getInteraction(
            interactionData.caches().get(rand.nextInt(interactionData.caches().size())).interactionFile().toPath()
        );

        if (interaction == null) {
            // Select random actor in data and send reply message for no interaction
            replyUnknownInteraction(interactionData.actors(), event.getMessage());
            logger.info("No interaction found, sending unknown interaction message.");
            return;
        }

        // Randomly select a dialogue from interaction dialogues
        List<Dialogue> dialogues = interaction.getDialogues();
        System.out.println(dialogues);
        Dialogue chosenDialogue = null;
        if (dialogues.size() == 1) {
            chosenDialogue = dialogues.get(0);
        } else {
            // Calculate total weight
            int totalWeight = 0;
            for (Dialogue dialogue : dialogues) {
                totalWeight += dialogue.getChance();
            }

            int random = (int) (Math.random() * totalWeight);
            int currentWeight = 0;
            for (Dialogue dialogue : dialogues) {
                currentWeight += dialogue.getChance();
                if (currentWeight >= random) {
                    chosenDialogue = dialogue;
                    break;
                }
            }
        }

        if (chosenDialogue == null) {
            // Select random actor in data and send reply message for no interaction
            replyUnknownInteraction(interactionData.actors(), event.getMessage());
            logger.info("No dialogue found, sending unknown interaction message.");
            return;
        }

        // Execute dialogue for interaction
        DialoguePlayer.setup(clubMap, chosenDialogue).play(event.getMessage(), event.getMember());
    }

    private void replyUnknownInteraction(List<Club> actors, Message message) {
        // Select random actor to send message
        Random rand = new Random();
        JDA actor = clubMap.get(actors.get(rand.nextInt(actors.size())));
        if (actor == null) {
            return;
        }

        // Send unknown interaction reply to message
        GuildMessageChannelUnion channel = actor.getChannelById(GuildMessageChannelUnion.class, message.getChannelId());
        if (channel == null ) {
            return;
        }

        int delay = DialoguePlayer.getReadingDelay(message.getContentRaw(), 1.0);
        channel.sendTyping().completeAfter(
            delay,
            TimeUnit.SECONDS
        );

        // Retrieve wpm
        int wpm;
        try {
            wpm = Integer.parseInt(new ConfigData(new DialogueConfig()).getConfig("wpm"));
        } catch (NumberFormatException e) {
            wpm = 40;
        }

        String response = "Huh?";
        delay += DialoguePlayer.getTypingDelay(response, 1.0, wpm);

        channel.sendMessage(response).completeAfter(
            delay,
            TimeUnit.SECONDS
        );
    }

    private InteractionData extractInteractionData(List<Member> mentioned, List<String> keys) {
        // Add all mentioned members to keys if they are actors
        if (!mentioned.isEmpty()) {
            for (Member member : mentioned) {
                switch (member.getEffectiveName()) {
                    case "Inori":
                        keys.add(Club.INORI.name());
                        break;

                    case "Suu":
                        keys.add(Club.SUU.name());
                        break;

                    case "Des":
                        keys.add(Club.DES.name());
                        break;

                    case "Crow":
                        keys.add(Club.CROW.name());
                        break;
                }
            }
        }

        // Retrieve all actors from keys
        List<Club> actors = new ArrayList<>();
        for (String key : keys) {
            // Handle getting actors
            Club actor = getActorFromKey(key);
            if (actor != null) {
                if (!actors.contains(actor)) {
                    actors.add(actor);
                }
            }
        }

        // Get interaction caches that match requirements
        List<InteractionCache> foundCaches = new ArrayList<>();
        for (InteractionCache cache : interactionCaches) {
            // Check for required actors
            if (!new HashSet<>(cache.actors()).containsAll(actors)) continue;

            // Check for required words
            if (cache.requiredMatches() < matchingWords(keys, cache.matchWords())) continue;

            // Add interaction data
            foundCaches.add(cache);
        }

        return new InteractionData(actors, foundCaches);
    }

    private Club getActorFromKey(String key) {
        // Check Inori
        for (String name : ClubNicknames.getInori()) {
            if (key.toLowerCase().contains(name.toLowerCase())) return Club.INORI;
        }

        // Check Suu
        for (String name : ClubNicknames.getSuu()) {
            if (key.toLowerCase().contains(name.toLowerCase())) return Club.SUU;
        }

        // Check Des
        for (String name : ClubNicknames.getDes()) {
            if (key.toLowerCase().contains(name.toLowerCase())) return Club.DES;
        }

        // Check Crow
        for (String name : ClubNicknames.getCrow()) {
            if (key.toLowerCase().contains(name.toLowerCase())) return Club.CROW;
        }

        return null;
    }

    private int matchingWords(List<String> keys, List<String> words) {
        int matches = 0;
        for (String key : keys) {
            if (words.contains(key)) matches++;
        }

        return matches;
    }

    private record InteractionData(List<Club> actors, List<InteractionCache> caches) {}
}
