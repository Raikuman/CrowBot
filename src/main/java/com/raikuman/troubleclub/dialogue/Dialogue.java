package com.raikuman.troubleclub.dialogue;

import com.raikuman.botutilities.config.ConfigData;
import com.raikuman.troubleclub.Club;
import com.raikuman.troubleclub.dialogue.config.DialogueConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Dialogue {

    private final HashMap<Club, GuildMessageChannelUnion> actors;
    private final List<Line> lines;

    public Dialogue() {
        this.actors = new HashMap<>();
        this.lines = new ArrayList<>();
    }

    public void addActor(Club club, JDA jda) {
        ConfigData config = new ConfigData(new DialogueConfig());
        Guild guild = jda.getGuildById(config.getConfig("targetguild"));
        if (guild == null) return;

        GuildMessageChannelUnion channel = guild.getChannelById(GuildMessageChannelUnion.class, config.getConfig("targetchannel"));
        if (channel == null) return;

        this.actors.put(club, channel);
    }

    public boolean checkForActor(Club club) {
        return actors.get(club) != null;
    }

    public void addLine(Club actor, String text, GuildSticker sticker, double speed) {
        GuildMessageChannelUnion channel = actors.get(actor);
        if (channel == null) return;

        this.lines.add(new Line(channel, text, sticker, speed));
    }

    public void play() {
        int wpm;
        try {
           wpm = Integer.parseInt(new ConfigData(new DialogueConfig()).getConfig("wpm"));
        } catch (NumberFormatException e) {
            wpm = 40;
        }

        for (Line line : lines) {
            // Send
            if (line.sticker != null) {
                line.channel.sendStickers(line.sticker).completeAfter(
                    1,
                    TimeUnit.SECONDS
                );
            } else {
                double doubleDelay = Math.pow(line.text.length(), 1 / (wpm * 5 / 60.0)) * line.speed;
                int delay;
                if (line.text.length() > 10) {
                    delay = (int) Math.ceil(doubleDelay);
                } else {
                    delay = (int) doubleDelay;
                }

                line.channel.sendTyping().complete();
                line.channel.sendMessage(line.text).completeAfter(
                    delay,
                    TimeUnit.SECONDS
                );
            }
        }
    }

    public record Line(GuildMessageChannelUnion channel, String text, GuildSticker sticker, double speed) {
    }
}