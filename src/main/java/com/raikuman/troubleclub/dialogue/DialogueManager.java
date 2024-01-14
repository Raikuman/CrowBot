package com.raikuman.troubleclub.dialogue;

import com.raikuman.troubleclub.Club;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DialogueManager {
    private static final Logger logger = LoggerFactory.getLogger(DialogueManager.class);
    private final ScheduledExecutorService executor;
    private HashMap<Club, JDA> clubMap;
    private final File playedDialogues, conversations;

    public DialogueManager(ScheduledExecutorService executor) {
        this.executor = executor;

        // Create file to hold played dialogues
        playedDialogues = new File("resources" + File.separator + "playeddialogues.txt");
        try {
            if (playedDialogues.createNewFile()) {
                logger.info("Created file {}", playedDialogues.getAbsolutePath());
            } else {
                logger.error("Failed to create file {}", playedDialogues.getAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("Error creating file {}", playedDialogues.getAbsolutePath());
        }

        conversations = new File("resources" + File.separator + "conversations");
        try {
            if (conversations.createNewFile()) {
                logger.info("Created file {}", conversations.getAbsolutePath());
            } else {
                logger.error("Failed to create file {}", conversations.getAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("Error creating file {}", conversations.getAbsolutePath());
        }
    }

    public void setClubMap(HashMap<Club, JDA> clubMap) {
        this.clubMap = clubMap;
    }

    public void beginTask() {
        executor.schedule(this::runDialogue, calculateDelay(), TimeUnit.MILLISECONDS);
    }

    private long calculateDelay() {
        return 0L;
    }

    private void runDialogue() {
        handleDialogue(false);

    }

    public void testDialogue() {
        handleDialogue(true);
    }

    private void handleDialogue(boolean ignoreAlreadyPlayed) {
        // Get the already played dialogues
        List<String> alreadyPlayed;
        if (!ignoreAlreadyPlayed) {
            alreadyPlayed = getAlreadyPlayed();
        } else {
            alreadyPlayed = new ArrayList<>();
        }

        // Get dialogues ready to be played
        File[] files = conversations.listFiles();
        if (files == null) return;

        List<File> ready = Arrays.stream(files).filter((file) ->
            !alreadyPlayed.contains(file.getName())).toList();

        // Play a random dialogue
        File dialogueFile = ready.get((int) (Math.random() * ready.size()));
        playDialogue(dialogueFile);

        // Add dialogue to already played dialogues
        if (!ignoreAlreadyPlayed) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(playedDialogues, true))) {
                bufferedWriter.write(dialogueFile.getName());
            } catch (IOException e) {
                logger.error("Error reading file {}", playedDialogues.getAbsolutePath());
            }
        }
    }

    private void playDialogue(File file) {
        if (clubMap == null) return;

        // Get dialogue
        Dialogue dialogue = ParseDialogue.parseDialogue(clubMap, file);
        if (dialogue == null) return;

        dialogue.play();
    }

    private List<String> getAlreadyPlayed() {
        if (!playedDialogues.exists()) {
            return new ArrayList<>();
        }

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(playedDialogues))) {
            return bufferedReader.lines().toList();
        } catch (IOException e) {
            logger.error("Error reading file {}", playedDialogues.getAbsolutePath());
            return new ArrayList<>(); // Return empty list if error reading file
        }
    }
}
