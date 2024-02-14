package com.raikuman.troubleclub.conversation;

import com.raikuman.botutilities.config.ConfigData;
import com.raikuman.troubleclub.conversation.config.DayWeights;
import com.raikuman.troubleclub.conversation.config.ConversationConfig;
import com.raikuman.troubleclub.conversation.config.HourWeights;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ConversationScheduling {

    private static final Logger logger = LoggerFactory.getLogger(ConversationScheduling.class);

    public static LocalTime generateScheduledTime() {
        List<HourWeight> hourWeights = getHourWeights();
        int totalWeight = 0;
        for (HourWeight hourWeight : hourWeights) {
            totalWeight += hourWeight.weight;
        }

        Random rng = new Random();
        int random = (int) (Math.random() * totalWeight);
        int currentWeight = 0;
        for (HourWeight hourWeight : hourWeights) {
            currentWeight += hourWeight.weight;
            if (currentWeight >= random) {
                return LocalTime.of(hourWeight.hour, rng.nextInt(60), rng.nextInt(60));
            }
        }

        return null;
    }

    public static LocalDate generateScheduledDate() {
        List<DayWeight> dayWeights = getDayWeights();
        int totalWeight = 0;
        for (DayWeight dayweight : dayWeights) {
            totalWeight += dayweight.weight;
        }

        int weeksBetween = 1;
        try {
            weeksBetween = Integer.parseInt(new ConfigData(new ConversationConfig()).getConfig("weeksbetween"));
        } catch (NumberFormatException e) {
            logger.error("Error parsing weeks between config");
        }

        LocalDate scheduledDate = null;
        int random = (int) (Math.random() * totalWeight);
        int currentWeight = 0;
        for (DayWeight dayweight : dayWeights) {
            currentWeight += dayweight.weight;
            if (currentWeight >= random) {
                boolean isDaily = Boolean.parseBoolean(new ConfigData(new ConversationConfig()).getConfig("daily"));
                if (isDaily) {
                    // Calculate days from current day next day
                    scheduledDate = LocalDate.now().plusDays(
                        dayweight.day() - LocalDate.now().getDayOfWeek().getValue()
                    );
                } else {
                    // Calculate days from current day next week
                    scheduledDate = LocalDate.now().plusDays(
                        (LocalDate.now().getDayOfWeek().getValue() - (7L * weeksBetween)) + dayweight.day
                    );
                }
                break;
            }
        }

        if (scheduledDate == null) {
            logger.error("Error generating scheduled date");
            return null;
        }

        File dateFile = new File("resources" + File.separator + "schedConversation.txt");
        if (!dateFile.exists()) {
            logger.info("Scheduled date file does not exist");
            return null;
        }

        // Print date to file
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(dateFile))) {
            bufferedWriter.write(scheduledDate.toString());
        } catch (IOException e) {
            logger.error("Error writing date {} to file {}", scheduledDate, dateFile.getAbsolutePath());
        }

        return scheduledDate;
    }

    private static List<HourWeight> getHourWeights() {
        List<HourWeight> hourWeights = new ArrayList<>();
        ConfigData config = new ConfigData(new HourWeights());
        for (int i = 0; i < 24; i++) {
            // Only get hours after current time
            if ((i + 1) < LocalTime.now().getHour()) continue;

            try {
                int weight = Integer.parseInt(config.getConfig(String.valueOf(i + 1)));
                if (weight == 0) continue;

                hourWeights.add(new HourWeight(i + 1, weight));
            } catch (NumberFormatException e) {
                logger.error("Error parsing weight for hour {}", i + 1);
            }
        }

        Collections.shuffle(hourWeights);
        return hourWeights;
    }

    private static List<DayWeight> getDayWeights() {
        boolean isDaily = Boolean.parseBoolean(new ConfigData(new ConversationConfig()).getConfig("daily"));

        List<DayWeight> dayWeights = new ArrayList<>();
        ConfigData config = new ConfigData(new DayWeights());
        for (int i = 0; i < 7; i++) {
            if (isDaily) {
                if ((i + 1) < LocalDate.now().getDayOfWeek().getValue()) continue;
            }

            try {
                int weight = Integer.parseInt(config.getConfig(String.valueOf(i + 1)));
                if (weight == 0) continue;

                dayWeights.add(new DayWeight(i + 1, weight));
            } catch (NumberFormatException e) {
                logger.error("Error parsing weight for day {}", i + 1);
            }
        }

        Collections.shuffle(dayWeights);
        return dayWeights;
    }

    record HourWeight(int hour, int weight) {
    }

    record DayWeight(int day, int weight) {
    }
}
