package com.raikuman.troubleclub.tamagopet.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageEditor {

    private static final Logger logger = LoggerFactory.getLogger(ImageEditor.class);

    public static BufferedImage loadImage(File file) {
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            logger.error("Could not load file from {}", file.getAbsolutePath());
            return null;
        }
    }

    public static BufferedImage superimposeImages(BufferedImage base, BufferedImage layer, int x, int y) {
        BufferedImage newImage = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);

        // Draw layer over base
        Graphics2D graphics2D = newImage.createGraphics();
        graphics2D.drawImage(base, 0, 0, null);
        graphics2D.drawImage(layer, x, y, null);
        graphics2D.dispose();

        return newImage;
    }
//
//    public static FileUpload getBenImage(CommandContext ctx) {
//        BufferedImage baseImage, newImage;
//        Graphics2D graphics2D;
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//        try {
//            baseImage = ImageIO.read(new URL(ctx.event().getAuthor().getEffectiveAvatarUrl()));
//        } catch (IOException e) {
//            logger.error("Could not retrieve base image from: {}", ctx.event().getAuthor().getEffectiveAvatarUrl());
//            return null;
//        }
//
//        // Create new image
//        newImage = new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
//        graphics2D = newImage.createGraphics();
//        graphics2D.drawImage(baseImage, 0, 0, null);
//
//        // Update image
//        graphics2D.setColor(Color.white);
//        graphics2D.fillRect(0, 0, 50, 50);
//
//        // Dispose g2d
//        graphics2D.dispose();
//
//        // Retrieve image
//        try {
//            ImageIO.write(newImage, "png", baos);
//            byte[] bytes = baos.toByteArray();
//
//            return FileUpload.fromData(bytes, "ben.png");
//        } catch (IOException e) {
//            logger.error("Could not create image");
//            return null;
//        }
//    }
}
