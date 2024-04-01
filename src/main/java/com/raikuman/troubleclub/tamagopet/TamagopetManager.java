package com.raikuman.troubleclub.tamagopet;

import com.raikuman.botutilities.config.ConfigData;
import com.raikuman.botutilities.invocation.component.ComponentBuilder;
import com.raikuman.botutilities.invocation.component.ComponentHandler;
import com.raikuman.botutilities.invocation.type.ButtonComponent;
import com.raikuman.botutilities.invocation.type.SelectComponent;
import com.raikuman.troubleclub.invoke.category.Tamagopet;
import com.raikuman.troubleclub.tamagopet.config.TamagopetConfig;
import com.raikuman.troubleclub.tamagopet.event.TamagopetEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TamagopetManager {

    private static final Logger logger = LoggerFactory.getLogger(TamagopetManager.class);
    private final ConfigData tamagoConfig;
    private final int pointsPerCharacter, pointsPerImage, maxHappiness;
    private final File tamagopetFile;
    private final TamagopetData tamagopetData;
    private final ComponentHandler componentHandler;
    private final List<TamagopetEvent> normalEvent, enragedEvent;
    private int noEventProcs = 0;

    protected TamagopetManager(ComponentHandler componentHandler) {
        this.componentHandler = componentHandler;

        // Load config
        tamagoConfig = new ConfigData(new TamagopetConfig());

        // Handle data
        this.pointsPerCharacter = getConfigDefault(tamagoConfig, "pointstext", 1);
        this.pointsPerImage = getConfigDefault(tamagoConfig, "pointsimage", 20);
        this.maxHappiness = getConfigDefault(tamagoConfig, "maxhappiness", 100);

        // Load Tamagopet data
        File file = new File("resources" + File.separator + "tamagopetData.txt");
        TamagopetData data;
        try {
            if (file.createNewFile()) {
                logger.info("Created file {}", file.getAbsolutePath());
                data = new TamagopetData();
            } else {
                logger.info("File {} exists", file.getAbsolutePath());
                data = loadTamagopetData();
            }
        } catch (IOException e) {
            logger.error("Error creating file {}", file.getAbsolutePath());
            file = null;
            data = new TamagopetData();
        }

        tamagopetFile = file;
        tamagopetData = data;

        saveTamagopetData();

        // Load events
        //normalEvent = List.of(new TamagopetFeed());
        normalEvent = new ArrayList<>();
        enragedEvent = new ArrayList<>();
    }

    private int getConfigDefault(ConfigData configData, String config, int defaultInt) {
        try {
            return Integer.parseInt(configData.getConfig(config));
        } catch (NumberFormatException e) {
            return defaultInt;
        }
    }

    public void saveTamagopetData() {
        if (tamagopetFile == null) {
            logger.error("No file to save Tamagopet data to");
            return;
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tamagopetFile))) {
            bufferedWriter.write(String.valueOf(tamagopetData.getHappiness()));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(tamagopetData.getTotalPoints()));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(tamagopetData.getPoints()));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(tamagopetData.getHealth()));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(tamagopetData.isEnraged()));
        } catch (IOException e) {
            logger.error("Could not save Tamagopet data to file: {}", tamagopetFile.getAbsolutePath());
        }
    }

    private TamagopetData loadTamagopetData() {
        if (tamagopetFile == null) {
            return new TamagopetData();
        }

        TamagopetData loadData = new TamagopetData();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(tamagopetFile))) {
            int currentData = 0;
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {

                switch (currentData) {
                    case 0:
                        try {
                            tamagopetData.setHappiness(Integer.parseInt(line));
                        } catch (NumberFormatException e) {
                            tamagopetData.setHappiness(0);
                        }
                        break;

                    case 1:
                        try {
                            tamagopetData.setTotalPoints(Integer.parseInt(line));
                        } catch (NumberFormatException e) {
                            tamagopetData.setTotalPoints(0);
                        }
                        break;

                    case 2:
                        try {
                            tamagopetData.setPoints(Integer.parseInt(line));
                        } catch (NumberFormatException e) {
                            tamagopetData.setPoints(0);
                        }
                        break;

                    case 3:
                        try {
                            tamagopetData.setHealth(Integer.parseInt(line));
                        } catch (NumberFormatException e) {
                            tamagopetData.setHealth(0);
                        }
                        break;

                    case 4:
                        tamagopetData.setEnraged(Boolean.parseBoolean(line));
                        break;
                }

                currentData++;
            }
        } catch (IOException e) {
            logger.error("Could not load Tamagopet data from file: {}", tamagopetFile.getAbsolutePath());
            return new TamagopetData();
        }

        return loadData;
    }

    public void addHappiness(MessageChannelUnion channel, int happiness) {
        tamagopetData.addHappiness(happiness);

        if (tamagopetData.getHappiness() >= 100) {
            tamagopetData.setEnraged(true);

            // Enrage notice
            channel.sendMessageEmbeds(
                new EmbedBuilder()
                    .setColor(Tamagopet.BEN_COLOR)
                    .setFooter("#" + channel.getName())
                    .setTimestamp(Instant.now())
                    .setAuthor("Ben has become enraged!")
                    .setDescription("Ben transformed into Bengrammaz!")
                    .setImage("attachment://ben.png").build()
            ).queue();
        }

        saveTamagopetData();
    }

    public void reduceHealth(MessageChannelUnion channel, int health) {
        tamagopetData.reduceHealth(health);

        if (tamagopetData.getHealth() <= 0) {
            tamagopetData.setEnraged(false);

            // Normal notice
            channel.sendMessageEmbeds(
                new EmbedBuilder()
                    .setColor(Tamagopet.BEN_COLOR)
                    .setFooter("#" + channel.getName())
                    .setTimestamp(Instant.now())
                    .setAuthor("Bengrammaz has reduced in size!")
                    .setDescription("Bengrammaz transformed back into Ben!")
                    .setImage("attachment://ben.png").build()
            ).queue();
        }

        saveTamagopetData();
    }

    protected void handleTamagopet(Message message, boolean forceEvent) {
        if (message.getAuthor().isBot()) {
            return;
        }

        handleEvent(message, forceEvent);

        // Save data
        saveTamagopetData();
    }

    private void handleEvent(Message message, boolean forceEvent) {
        // Break down message
        String text = message.getContentRaw().replaceAll("[^a-zA-Z0-9]", "");
        List<Message.Attachment> attachments = message.getAttachments();

        // Handle points
        int points = 0;
        points += text.length() * pointsPerCharacter;
        points += attachments.size() * pointsPerImage;
        tamagopetData.addPoints(points);

        double chance = Math.pow(tamagopetData.getPoints() / 100.0, 2);
        Random rand = new Random();
        double randomValue = 100.0 * rand.nextDouble();

        if (chance * (noEventProcs + 1) >= randomValue || forceEvent) {
            // Do event
            noEventProcs = 0;
            tamagopetData.setPoints(0);

            if (tamagopetData.isEnraged()) {
                if (enragedEvent.isEmpty()) {
                    return;
                }

                playEvent(enragedEvent.get(
                    rand.nextInt(enragedEvent.size())),
                    message);
            } else {
                if (normalEvent.isEmpty()) {
                    return;
                }

                playEvent(normalEvent.get(
                    rand.nextInt(normalEvent.size())),
                    message);
            }
        } else {
            // Don't do event
            noEventProcs++;
        }
    }

    private void playEvent(TamagopetEvent event, Message message) {
        MessageCreateAction embedImageAction = embedImageAction(
            new EmbedBuilder()
                .setColor(Tamagopet.BEN_COLOR)
                .setFooter("#" + message.getChannel().getName())
                .setTimestamp(Instant.now())
                .setAuthor(event.title())
                .setDescription(event.description()),
            event.getImage(),
            message.getChannel());

        List<ActionRow> actionRows = new ArrayList<>();
        List<ButtonComponent> buttons = event.getButtons(this);
        if (!buttons.isEmpty()) {
            componentHandler.addButtons(message.getAuthor(), message, buttons);
            actionRows.add(ComponentBuilder.buildButtons(message.getAuthor(), buttons));
        }

        List<SelectComponent> selects = event.getSelects(tamagopetData);
        if (!selects.isEmpty()) {
            String menuPlaceholder;
            if (event.selectMenuPlaceholder().isEmpty()) {
                menuPlaceholder = "Options";
            } else {
                menuPlaceholder = event.selectMenuPlaceholder();
            }

            componentHandler.addSelects(message.getAuthor(), message, selects);
            actionRows.add(ComponentBuilder.buildStringSelectMenu(
                "tamagopetevent" + event.hashCode(),
                menuPlaceholder,
                message.getAuthor(),
                selects
            ));
        }

        embedImageAction.setComponents(actionRows).queue();
    }

    public static MessageCreateAction embedImageAction(EmbedBuilder embedBuilder, FileUpload image, MessageChannelUnion channel) {
        embedBuilder.setImage("attachment://" + image.getName());
        return channel.sendFiles(image).setEmbeds(embedBuilder.build());
    }
}
