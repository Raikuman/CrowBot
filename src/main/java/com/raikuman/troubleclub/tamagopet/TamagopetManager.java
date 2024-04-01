package com.raikuman.troubleclub.tamagopet;

import com.raikuman.botutilities.config.ConfigData;
import com.raikuman.botutilities.invocation.component.ComponentBuilder;
import com.raikuman.botutilities.invocation.component.ComponentHandler;
import com.raikuman.botutilities.invocation.type.ButtonComponent;
import com.raikuman.botutilities.invocation.type.SelectComponent;
import com.raikuman.troubleclub.invoke.category.Tamagopet;
import com.raikuman.troubleclub.tamagopet.config.TamagopetConfig;
import com.raikuman.troubleclub.tamagopet.event.TamagopetEvent;
import com.raikuman.troubleclub.tamagopet.event.normal.TamagopetFeed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
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

    public TamagopetManager(ComponentHandler componentHandler) {
        this.componentHandler = componentHandler;

        // Load config
        tamagoConfig = new ConfigData(new TamagopetConfig());

        // Handle data
        this.pointsPerCharacter = getConfigDefault(tamagoConfig, "pointstext", 1);
        this.pointsPerImage = getConfigDefault(tamagoConfig, "pointsimage", 20);
        this.maxHappiness = getConfigDefault(tamagoConfig, "maxhappiness", 100);

        // Load Ben data
        File file = new File("resources" + File.separator + "tamagopetData.txt");
        TamagopetData data;
        try {
            if (file.createNewFile()) {
                logger.info("Created file {}", file.getAbsolutePath());
                data = new TamagopetData();
                saveBenData(data, file);
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

        // Load events
        normalEvent = List.of(new TamagopetFeed());
        enragedEvent = new ArrayList<>();
    }

    private int getConfigDefault(ConfigData configData, String config, int defaultInt) {
        try {
            return Integer.parseInt(configData.getConfig(config));
        } catch (NumberFormatException e) {
            return defaultInt;
        }
    }

    private void saveBenData(TamagopetData benData, File benFile) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(benFile))) {
            bufferedWriter.write(String.valueOf(benData.getHappiness()));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(benData.getPoints()));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(benData.getHealth()));
            bufferedWriter.newLine();
            bufferedWriter.write(String.valueOf(benData.isEnraged()));
        } catch (IOException e) {
            logger.error("Could not save Ben data to file: {}", benFile.getAbsolutePath());
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
                            tamagopetData.setPoints(Integer.parseInt(line));
                        } catch (NumberFormatException e) {
                            tamagopetData.setPoints(0);
                        }
                        break;

                    case 2:
                        try {
                            tamagopetData.setHealth(Integer.parseInt(line));
                        } catch (NumberFormatException e) {
                            tamagopetData.setHealth(0);
                        }
                        break;

                    case 3:
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

    public void handleTamagopet(Message message, boolean forceEvent) {
        if (message.getAuthor().isBot()) {
            return;
        }

        handleEvent(message, forceEvent);

        // Save data
        saveBenData(tamagopetData, tamagopetFile);
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
        MessageCreateAction messageCreateAction = message.getChannel().sendFiles(event.getImage()).setEmbeds(
            getTamagopetEmbed(
                event,
                message.getChannel()
            ).build());

        List<ActionRow> actionRows = new ArrayList<>();
        List<ButtonComponent> buttons = event.getButtons(tamagopetData);
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

        messageCreateAction.setComponents(actionRows).queue();
    }

    private EmbedBuilder getTamagopetEmbed(TamagopetEvent event, MessageChannelUnion channel) {
        return new EmbedBuilder()
            .setColor(Tamagopet.BEN_COLOR)
            .setFooter("#" + channel.getName())
            .setTimestamp(Instant.now())
            .setAuthor(event.title())
            .setDescription(event.description())
            .setImage("attachment://ben.png");
    }
}
