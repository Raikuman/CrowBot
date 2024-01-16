package com.raikuman.troubleclub.dialogue;

import com.raikuman.botutilities.config.ConfigData;
import com.raikuman.troubleclub.Club;
import com.raikuman.troubleclub.dialogue.config.HourWeights;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DialogueManager {
    private static final Logger logger = LoggerFactory.getLogger(DialogueManager.class);
    private final ScheduledExecutorService executor;
    private HashMap<Club, JDA> clubMap;
    private final File playedDialogues, conversations, scheduledDate;

    public DialogueManager(ScheduledExecutorService executor) {
        this.executor = executor;

        // Create file to hold played dialogues
        playedDialogues = new File("resources" + File.separator + "playeddialogues.txt");
        dialogueFileCheck(playedDialogues);

        // Directory for conversations
        conversations = new File("resources" + File.separator + "conversations");
        dialogueFileCheck(conversations);

        // Create file for a scheduled date
        scheduledDate = new File("resources" + File.separator + "scheduleddate.txt");
        dialogueFileCheck(scheduledDate);
    }

    private void dialogueFileCheck(File file) {
        try {
            if (file.createNewFile()) {
                logger.info("Created file {}", file.getAbsolutePath());
            } else {
                logger.info("File {} exists", file.getAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("Error creating file {}", file.getAbsolutePath());
        }
    }

    public void setClubMap(HashMap<Club, JDA> clubMap) {
        this.clubMap = clubMap;
    }

    public void beginTask() {
        LocalDate scheduledDate = getScheduledDate();
        if (scheduledDate == null) {
            logger.error("Error getting scheduled date");
            return;
        }

        LocalTime scheduledTime = DialogueScheduling.generateScheduledTime();
        if (!LocalDate.now().equals(scheduledDate)) {
            logger.info("Scheduled date is not today");
            return;
        } else {
            logger.info("Executing dialogue task on {}", scheduledTime);
        }

        executor.schedule(
            this::runDialogue,
            ChronoUnit.MILLIS.between(LocalTime.now(), scheduledTime),
            TimeUnit.MILLISECONDS);
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

    private LocalDate getScheduledDate() {
        String dateString;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(scheduledDate))) {
            dateString = bufferedReader.readLine();
        } catch (IOException e) {
            logger.error("Error reading file {}", scheduledDate.getAbsolutePath());
            dateString = "";
        }

        LocalDate scheduledDate;
        if (dateString == null) {
            scheduledDate = DialogueScheduling.generateScheduledDate();
        } else {
            // Parse string to date
            scheduledDate = LocalDate.parse(dateString);
        }

        return scheduledDate;
    }
}
