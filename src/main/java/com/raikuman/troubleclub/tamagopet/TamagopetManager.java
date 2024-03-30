package com.raikuman.troubleclub.tamagopet;

import com.raikuman.botutilities.config.ConfigData;
import com.raikuman.botutilities.invocation.component.ComponentHandler;
import com.raikuman.troubleclub.tamagopet.config.TamagopetConfig;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Random;

public class TamagopetManager {

    private static final Logger logger = LoggerFactory.getLogger(TamagopetManager.class);
    private final ConfigData tamagoConfig;
    private final int pointsPerCharacter, pointsPerImage, maxHappiness;
    private final File benFile;
    private final BenData benData;
    private final ComponentHandler componentHandler;
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
        File file = new File("resources" + File.separator + "benData.txt");
        BenData data;
        try {
            if (file.createNewFile()) {
                logger.info("Created file {}", file.getAbsolutePath());
                data = new BenData();
                saveBenData(data, file);
            } else {
                logger.info("File {} exists", file.getAbsolutePath());
                data = loadBenData();
            }
        } catch (IOException e) {
            logger.error("Error creating file {}", file.getAbsolutePath());
            file = null;
            data = new BenData();
        }

        benFile = file;
        benData = data;
    }

    private int getConfigDefault(ConfigData configData, String config, int defaultInt) {
        try {
            return Integer.parseInt(configData.getConfig(config));
        } catch (NumberFormatException e) {
            return defaultInt;
        }
    }

    private void saveBenData(BenData benData, File benFile) {
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

    private BenData loadBenData() {
        if (benFile == null) {
            return new BenData();
        }

        BenData loadData = new BenData();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(benFile))) {
            int currentData = 0;
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {

                switch (currentData) {
                    case 0:
                        try {
                            benData.setHappiness(Integer.parseInt(line));
                        } catch (NumberFormatException e) {
                            benData.setHappiness(0);
                        }
                        break;

                    case 1:
                        try {
                            benData.setPoints(Integer.parseInt(line));
                        } catch (NumberFormatException e) {
                            benData.setPoints(0);
                        }
                        break;

                    case 2:
                        try {
                            benData.setHealth(Integer.parseInt(line));
                        } catch (NumberFormatException e) {
                            benData.setHealth(0);
                        }
                        break;

                    case 3:
                        benData.setEnraged(Boolean.parseBoolean(line));
                        break;
                }

                currentData++;
            }
        } catch (IOException e) {
            logger.error("Could not load Ben data from file: {}", benFile.getAbsolutePath());
            return new BenData();
        }

        return loadData;
    }

    public void handleTamagopet(Message message) {
        if (message.getAuthor().isBot()) {
            return;
        }

        // Break down message
        String text = message.getContentRaw().replaceAll("[^a-zA-Z0-9]", "");
        List<Message.Attachment> attachments = message.getAttachments();

        // Handle points
        int points = 0;
        points += text.length() * pointsPerCharacter;
        points += attachments.size() * pointsPerImage;
        benData.addPoints(points);

        handleEvent(message.getMember(), message.getChannel(), benData.isEnraged());

        // Save data
        saveBenData(benData, benFile);
    }

    private void handleEvent(Member member, MessageChannelUnion channel, boolean isEnraged) {
        double chance = Math.pow(benData.getPoints() / 100.0, 2);
        Random rand = new Random();
        double randomValue = 100.0 * rand.nextDouble();

        if (chance * (noEventProcs + 1) <= randomValue) {
            // Do event
            noEventProcs = 0;
            benData.setPoints(0);

            if (isEnraged) {
                //TamagopetEventHandler.handleEnragedEvent(member, channel, componentHandler);
            } else {
                //TamagopetEventHandler.handleNormalEvent(member, channel, componentHandler);
            }

            saveBenData(benData, benFile);
        } else {
            // Don't do event
            noEventProcs++;
        }
    }
}

class BenData {
    private int happiness, points, health;
    private boolean isEnraged;

    public BenData() {
        this.happiness = 0;
        this.points = 0;
        this.health = 100;
        this.isEnraged = false;
    }

    public int getHappiness() {
        return happiness;
    }

    public int getPoints() {
        return points;
    }

    public int getHealth() {
        return health;
    }

    public boolean isEnraged() {
        return isEnraged;
    }

    public void setHappiness(int happiness) {
        this.happiness = happiness;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setEnraged(boolean enraged) {
        isEnraged = enraged;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void changeHealth(int healthMod) {
        this.health += healthMod;
        if (this.health < 0) {
            this.health = 0;
        }
    }
}
