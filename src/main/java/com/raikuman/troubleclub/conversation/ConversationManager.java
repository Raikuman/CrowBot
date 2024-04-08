package com.raikuman.troubleclub.conversation;

import com.raikuman.botutilities.config.ConfigData;
import com.raikuman.troubleclub.conversation.config.ConversationConfig;
import com.raikuman.troubleclub.dialogue.Dialogue;
import com.raikuman.troubleclub.dialogue.DialoguePlayer;
import com.raikuman.troubleclub.Club;
import com.raikuman.troubleclub.dialogue.DialogueParser;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConversationManager {
    private static final Logger logger = LoggerFactory.getLogger(ConversationManager.class);
    private final ScheduledExecutorService executor;
    private HashMap<Club, JDA> clubMap;
    private final File playedConversations, conversations, scheduledDate;

    public ConversationManager(ScheduledExecutorService executor) {
        this.executor = executor;

        // Create file to hold played dialogues
        playedConversations = new File("resources" + File.separator + "playedConversations.txt");
        dialogueFileCheck(playedConversations);

        // Directory for conversations
        File conversations;
        try {
            conversations = Files.createDirectories(Path.of("resources" + File.separator + "conversations")).toFile();
        } catch (IOException e) {
            conversations = null;
            logger.error("Could not create directory for conversations");
        }
        this.conversations = conversations;

        // Create file for a scheduled date
        scheduledDate = new File("resources" + File.separator + "schedConversation.txt");
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

    public HashMap<Club, JDA> getClubMap() {
        return clubMap;
    }

    public void beginTask() {
        LocalDate scheduledDate = getScheduledDate();
        if (scheduledDate == null) {
            logger.error("Error getting scheduled date");
            return;
        }

        // Check if date is now
        boolean justGenerated = false;
        if (!LocalDate.now().equals(scheduledDate)) {
            logger.info("Scheduled date is not today");

            // Check if date is passed
            if (LocalDate.now().isAfter(scheduledDate)) {
                // Generate a new date
                scheduledDate = ConversationScheduling.generateScheduledDate();
                justGenerated = true;
                logger.info("Scheduling new conversation date for: {}", scheduledDate);
            }
        }

        // Check if a schedule date was generated
        if (!justGenerated) {
            return;
        }

        // Check if the generated schedule date is today
        if (!LocalDate.now().equals(scheduledDate)) {
            return;
        }

        LocalTime scheduledTime = ConversationScheduling.generateScheduledTime();
        logger.info("Executing dialogue task on {}", scheduledTime);

        executor.schedule(
            this::runDialogue,
            ChronoUnit.MILLIS.between(LocalTime.now(), scheduledTime),
            TimeUnit.MILLISECONDS);
    }

    private void runDialogue() {
        handleDialogue(
            Boolean.parseBoolean(new ConfigData(new ConversationConfig()).getConfig("ignoreplayed"))
        );
    }

    private void handleDialogue(boolean ignoreAlreadyPlayed) {
        if (conversations == null) {
            return;
        }

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
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(playedConversations, true))) {
                bufferedWriter.write(dialogueFile.getName());
            } catch (IOException e) {
                logger.error("Error reading file {}", playedConversations.getAbsolutePath());
            }
        }
    }

    private void playDialogue(File file) {
        if (clubMap == null) return;

        // Get dialogue
        Conversation conversation = DialogueParser.getConversation(file.toPath());
        if (conversation == null) return;

        Dialogue dialogue = conversation.getDialogue();
        if (dialogue == null) return;

        DialoguePlayer.setup(getClubMap(), dialogue).play(null);
    }

    private List<String> getAlreadyPlayed() {
        if (!playedConversations.exists()) {
            return new ArrayList<>();
        }

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(playedConversations))) {
            return bufferedReader.lines().toList();
        } catch (IOException e) {
            logger.error("Error reading file {}", playedConversations.getAbsolutePath());
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
            scheduledDate = ConversationScheduling.generateScheduledDate();
        } else {
            // Parse string to date
            scheduledDate = LocalDate.parse(dateString);
        }

        return scheduledDate;
    }
}
