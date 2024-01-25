package com.raikuman.troubleclub.interaction;

import com.raikuman.troubleclub.Club;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Interaction {

    private final HashMap<Club, GuildMessageChannelUnion> actors;
    private final boolean isCommand;
    private final Club invoker;
    private final String command;
    private final List<Line> dialogue;

    public Interaction(HashMap<Club, GuildMessageChannelUnion> actors, boolean isCommand,
                       Club invoker, String command) {
        this.actors = actors;
        this.isCommand = isCommand;
        this.invoker = invoker;
        this.command = command;
        this.dialogue = new ArrayList<>();
    }

    public void addLine(Line line) {
        dialogue.add(line);
    }

    public List<Line> getDialogue() {
        return dialogue;
    }

    public record Line(GuildMessageChannelUnion actor, Emoji reaction, GuildSticker sticker, String text) {}
}
