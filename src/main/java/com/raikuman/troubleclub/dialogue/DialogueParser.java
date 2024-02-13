package com.raikuman.troubleclub.dialogue;

import com.alibaba.fastjson2.JSON;
import com.raikuman.troubleclub.conversation.Conversation;
import com.raikuman.troubleclub.interaction.Interaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DialogueParser {

    private static final Logger logger = LoggerFactory.getLogger(DialogueParser.class);

    public static Interaction getInteraction(Path path) {
        try {
            return JSON.parseObject(Files.readString(path), Interaction.class);
        } catch (IOException e) {
            logger.error("Could not parse interaction file: " + path);
        }

        return null;
    }

    public static Conversation getConversation(Path path) {
        try {
            return JSON.parseObject(Files.readString(path), Conversation.class);
        } catch (IOException e) {
            logger.error("Could not parse conversation file: " + path);
        }

        return null;
    }
}
