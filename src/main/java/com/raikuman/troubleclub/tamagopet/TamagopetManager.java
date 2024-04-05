package com.raikuman.troubleclub.tamagopet;

import com.alibaba.fastjson2.JSON;
import com.raikuman.botutilities.config.ConfigData;
import com.raikuman.botutilities.invocation.component.ComponentBuilder;
import com.raikuman.botutilities.invocation.component.ComponentHandler;
import com.raikuman.botutilities.invocation.type.ButtonComponent;
import com.raikuman.botutilities.invocation.type.SelectComponent;
import com.raikuman.troubleclub.invoke.category.Tamagopet;
import com.raikuman.troubleclub.tamagopet.config.TamagopetConfig;
import com.raikuman.troubleclub.tamagopet.event.TamagopetEvent;
import com.raikuman.troubleclub.tamagopet.event.enraged.TamagopetMagical;
import com.raikuman.troubleclub.tamagopet.event.enraged.TamagopetPhysical;
import com.raikuman.troubleclub.tamagopet.event.normal.TamagopetBath;
import com.raikuman.troubleclub.tamagopet.event.normal.TamagopetFeed;
import com.raikuman.troubleclub.tamagopet.event.normal.TamagopetSpell;
import com.raikuman.troubleclub.tamagopet.image.TamagopetImage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.Duration;
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
                saveTamagopetData();
            } else {
                logger.info("File {} exists", file.getAbsolutePath());
                data = loadTamagopetData(file);
            }
        } catch (IOException e) {
            logger.error("Error creating file {}", file.getAbsolutePath());
            file = null;
            data = new TamagopetData();
        }

        tamagopetFile = file;
        tamagopetData = data;

        // Load events
        normalEvent = List.of(
            new TamagopetFeed(this),
            new TamagopetBath(this),
            new TamagopetSpell(this)
        );

        enragedEvent = List.of(
            new TamagopetPhysical(this),
            new TamagopetMagical(this)
        );
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
            bufferedWriter.write(JSON.toJSONString(tamagopetData));
        } catch (IOException e) {
            logger.error("Could not save Tamagopet data to file: {}", tamagopetFile.getAbsolutePath());
        }
    }

    private TamagopetData loadTamagopetData(File file) {
        if (file == null) {
            return new TamagopetData();
        }

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String data = bufferedReader.readLine();
            if (data == null) {
                return new TamagopetData();
            }

            TamagopetData loadedData = JSON.parseObject(data, TamagopetData.class);
            if (loadedData == null) {
                return new TamagopetData();
            }

            return loadedData;
        } catch (IOException e) {
            logger.error("Could not load Tamagopet data from file: {}", file.getAbsolutePath());
            return new TamagopetData();
        }
    }

    public void addHappiness(MessageChannelUnion channel, int happiness) {
        tamagopetData.addHappiness(happiness);

        if (tamagopetData.getHappiness() >= maxHappiness) {
            tamagopetData.setEnraged(true);
            tamagopetData.setHappiness(0);

            // Calculate health
            tamagopetData.setHealth(tamagopetData.getTotalPoints() * 2);
            tamagopetData.setTotalPoints(0);

            // Enrage notice
            sendEmbedImageAction(
                "Ben has become enraged!",
                "Ben transformed into Bengrammaz!",
                new TamagopetImage().retrieveAsFile(),
                channel
            ).delay(Duration.ofSeconds(10)).flatMap(Message::delete).queue();
        }

        saveTamagopetData();
    }

    public void reduceHealth(MessageChannelUnion channel, int health) {
        tamagopetData.reduceHealth(health);

        if (tamagopetData.getHealth() == 0) {
            tamagopetData.setEnraged(false);
            tamagopetData.setHealth(0);
            tamagopetData.setTotalPoints(0);

            // Normal notice
            sendEmbedImageAction(
                "Bengrammaz has reduced in size!",
                "Bengrammaz transformed back into Ben!",
                new TamagopetImage().retrieveAsFile(),
                channel
            ).delay(Duration.ofSeconds(10)).flatMap(Message::delete).queue();
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
        long points = 0;
        points += (long) text.length() * pointsPerCharacter;
        points += (long) attachments.size() * pointsPerImage;
        tamagopetData.addPoints(points);

        double chance = Math.pow(tamagopetData.getPoints() / 250.0, 2);
        Random rand = new Random();
        double randomValue = 100.0 * rand.nextDouble();

        if (chance * tamagopetData.getNoEventMulti() >= randomValue || forceEvent) {
            // Do event
            tamagopetData.setPoints(0);
            tamagopetData.resetEventMulti();

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
            tamagopetData.addEventMulti(points / 1000.0);
        }
    }

    private void playEvent(TamagopetEvent event, Message message) {
        MessageCreateAction embedImageAction = sendEmbedImageAction(
            event.title(),
            event.description(),
            event.getImage(),
            message.getChannel());

        List<ActionRow> actionRows = new ArrayList<>();
        List<ButtonComponent> buttons = event.getButtons();
        if (!buttons.isEmpty()) {
            componentHandler.addButtons(message.getAuthor(), message, buttons);
            actionRows.addAll(ComponentBuilder.buildButtons(message.getAuthor(), buttons));
        }

        List<SelectComponent> selects = event.getSelects();
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

    private MessageCreateAction sendEmbedImageAction(String title, String description, FileUpload image,
                                                           MessageChannelUnion channel) {
        return channel.sendFiles(image).setEmbeds(
            new EmbedBuilder()
                .setColor(Tamagopet.BEN_COLOR)
                .setFooter("#" + channel.getName())
                .setTimestamp(Instant.now())
                .setAuthor(title)
                .setDescription(description)
                .setImage("attachment://" + image.getName()).build()
        );
    }
}
