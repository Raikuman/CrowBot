package com.raikuman.troubleclub.tamagopet.image;

import com.raikuman.troubleclub.invoke.category.Tamagopet;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

public class TamagopetImage {

    private static final Logger logger = LoggerFactory.getLogger(TamagopetImage.class);
    private BufferedImage image;

    static {
        try {
            Files.createDirectories(Path.of("resources" + File.separator + "tamagopet"));
        } catch (IOException e) {
            logger.error("Could not create directory for tamagopet");
        }
    }

    public TamagopetImage() {
        this.image = ImageEditor.loadImage(new File(
            "resources" + File.separator + "tamagopet" + File.separator + "bendefault.png"
        ));
    }

    public TamagopetImage(String fileName) {
        String path = "resources" + File.separator + "tamagopet" + File.separator;
        if (fileName.isEmpty()) {
            // Load default
            path += "bendefault.png";
        } else {
            // Load from file
            path += fileName;
        }

        this.image = ImageEditor.loadImage(new File(path));
    }

    public void superimpose(String fileName, int x, int y) {
        if (x > image.getWidth()) {
            x = image.getWidth();
        }

        if (y > image.getHeight()) {
            y = image.getHeight();
        }

        drawOverImage(fileName, x, y);
    }

    public void superimpose(String fileName) {
        drawOverImage(fileName, 0, 0);
    }

    private void drawOverImage(String fileName, int x, int y) {
        if (image == null) {
            logger.error("Could not superimpose to an image that does not exist");
            return;
        }

        // Load image
        BufferedImage layer = ImageEditor.loadImage(
            new File("resources" + File.separator + "tamagopet" + File.separator + fileName));

        if (layer == null) {
            logger.error("Could not load new image to superimpose to the base image");
            return;
        }

        image = ImageEditor.superimposeImages(image, layer, x, y);
    }

    public FileUpload retrieveAsFile() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, "png", byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();

            return FileUpload.fromData(bytes, "ben.png");
        } catch (IOException e) {
            logger.error("Could not create file from image");
            return null;
        }
    }

    public static void handleEmbedUpdate(String title, String description, TamagopetImage tamagopetImage,
                                         StringSelectInteractionEvent ctx) {
        updateEmbeds(
            title,
            description,
            tamagopetImage,
            ctx.getMessage().getEmbeds().get(0),
            ctx.deferEdit());
    }

    public static void handleEmbedUpdate(String title, String description, TamagopetImage tamagopetImage,
                                         ButtonInteractionEvent ctx) {
        updateEmbeds(
            title,
            description,
            tamagopetImage,
            ctx.getMessage().getEmbeds().get(0),
            ctx.deferEdit());
    }

    private static void updateEmbeds(String title, String description, TamagopetImage tamagopetImage,
                                     MessageEmbed embed, MessageEditCallbackAction callbackAction) {
        MessageEmbed.AuthorInfo authorInfo = embed.getAuthor();
        EmbedBuilder embedBuilder = new EmbedBuilder(embed);

        if (authorInfo == null) {
            embedBuilder.setAuthor(title);
        } else {
            embedBuilder.setAuthor(title, null, authorInfo.getProxyIconUrl());
        }

        embedBuilder
            .setDescription(description)
            .setImage("attachment://" + tamagopetImage.retrieveAsFile().getName());

        callbackAction
            .setEmbeds(embedBuilder.build())
            .setComponents()
            .setFiles(tamagopetImage.retrieveAsFile())
            .delay(Duration.ofSeconds(10))
            .flatMap(InteractionHook::deleteOriginal).queue();
    }

    public static MessageCreateAction sendEmbedImageAction(String title, String description, FileUpload image,
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

