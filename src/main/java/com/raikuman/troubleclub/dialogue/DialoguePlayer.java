package com.raikuman.troubleclub.dialogue;

import com.raikuman.botutilities.config.ConfigData;
import com.raikuman.troubleclub.Club;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import org.slf4j.helpers.CheckReturnValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DialoguePlayer {

    private final ConfigData dialogueConfig;
    private final HashMap<Club, JDA> clubMap;
    private final Dialogue dialogue;
    private final List<LineData> lines;

    private DialoguePlayer(HashMap<Club, JDA> clubMap, Dialogue dialogue) {
        this.dialogueConfig = new ConfigData(new DialogueConfig());
        this.clubMap = clubMap;
        this.dialogue = dialogue;
        this.lines = new ArrayList<>();
        setupLines(dialogue);
    }

    @CheckReturnValue
    public static DialoguePlayer setup(HashMap<Club, JDA> clubMap, Dialogue dialogue) {
        return new DialoguePlayer(clubMap, dialogue);
    }

    private void setupLines(Dialogue dialogue) {
        for (Dialogue.Line line : dialogue.getLines()) {
            GuildMessageChannelUnion channel = getActorChannel(clubMap.get(line.getActor()), line.getTargetChannel());
            if (channel == null) continue;

            lines.add(
                new LineData(
                    channel,
                    line,
                    dialogueConfig
                )
            );
        }
    }

    private GuildMessageChannelUnion getActorChannel(JDA jda, long targetChannel) {
        // Get target guild from JDA
        Guild guild = jda.getGuildById(dialogueConfig.getConfig("targetguild"));
        if (guild == null) return null;

        GuildMessageChannelUnion channel;
        if (targetChannel == 0L) {
            // Use default channel
            channel = guild.getChannelById(GuildMessageChannelUnion.class, dialogueConfig.getConfig("targetchannel"));
        } else {
            // Use custom channel
            channel = guild.getChannelById(GuildMessageChannelUnion.class, targetChannel);
        }

        return channel;
    }

    public void play(Message previousMessage) {
        // Check if dialogue was set up correctly
        if (dialogue.getLines().size() != lines.size()) {
            return;
        }

        // Handle sending lines
        Message currentMessage = previousMessage;
        Club currentActor = null;
        for (LineData line : lines) {
            String currentText = currentMessage != null ? currentMessage.getContentRaw() : "";

            // Calculate reading previous message
            int delay;
            if (currentActor == line.line.getActor()) {
                delay = 0;
            } else {
                delay = getReadingDelay(currentText, line.getReadSpeed());
            }
            currentActor = line.line.getActor();

            // Handle reaction to previous message first
            if (line.getReaction() != null && currentMessage != null) {
                currentMessage.addReaction(line.getReaction()).completeAfter(
                    delay,
                    TimeUnit.SECONDS
                );

                delay++;
            }

            // Handle sending a sticker second
            if (line.sticker != null) {
                currentMessage = line.getChannel().sendStickers(line.getSticker()).completeAfter(
                    delay,
                    TimeUnit.SECONDS
                );

                delay++;
            }

            // Handle sending actor line third
            if (!line.getLine().isBlank()) {
                // Send typing
                line.getChannel().sendTyping().completeAfter(
                    delay,
                    TimeUnit.SECONDS
                );

                // Retrieve wpm
                int wpm;
                try {
                    wpm = Integer.parseInt(dialogueConfig.getConfig("wpm"));
                } catch (NumberFormatException e) {
                    wpm = 40;
                }

                // Calculate typing delay
                delay += getTypingDelay(line.getLine(), line.getTypeSpeed(), wpm);

                // Send actor line
                currentMessage = line.getChannel().sendMessage(line.getLine()).completeAfter(
                    delay,
                    TimeUnit.SECONDS
                );
            }
        }
    }

    public static int getReadingDelay(String message, double readSpeed) {
        double delay;
        if (message.length() > 10) {
            // Make longer messages have a greater delay
            delay = message.length() / 10.0;
        } else {
            delay = message.length() / 20.0;
        }

        // Calculate delay with variance
        Random rand = new Random();
        double variance = delay * 0.1;

        try {
            delay = delay + rand.nextDouble(variance * 2) - variance;
        } catch (IllegalArgumentException e) {
            delay = 0;
        }

        // Calculate delay with actor read speed
        delay *= readSpeed;

        // Always get ceiling of delay
        return (int) Math.ceil(delay);
    }

    public static int getTypingDelay(String message, double typeSpeed, int wpm) {
        // Calculate typing delay using function (x = message length, w = wpm, t = type speed):
        //      (x^(1/(w * 5 / 60)) * t
        return (int) Math.ceil(Math.pow(message.length(), 1 / (wpm * 5 / 60.0)) * typeSpeed);
    }

    public static class LineData {

        private final GuildMessageChannelUnion actorChannel;
        private final Dialogue.Line line;
        private final Emoji reaction;
        private final GuildSticker sticker;

        public LineData(GuildMessageChannelUnion actorChannel, Dialogue.Line line, ConfigData dialogueConfig) {
            this.actorChannel = actorChannel;
            this.line = line;

            if (line.getReaction().isBlank()) {
                reaction = null;
            } else {
                reaction = Emoji.fromFormatted(line.getReaction());
            }

            if (line.getSticker().isBlank()) {
                sticker = null;
            } else {
                sticker = actorChannel.getGuild().getStickerById(dialogueConfig.getConfig(line.getSticker() + "sticker"));
            }
        }

        public GuildMessageChannelUnion getChannel() {
            return actorChannel;
        }

        public Emoji getReaction() {
            return reaction;
        }

        public GuildSticker getSticker() {
            return sticker;
        }

        public String getLine() {
            return line.getLine();
        }

        public double getTypeSpeed() {
            return line.getTypeSpeed();
        }

        public double getReadSpeed() {
            return line.getReadSpeed();
        }
    }
}
