package com.raikuman.troubleclub.parser;

import com.raikuman.troubleclub.Club;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;

import java.util.ArrayList;
import java.util.List;

public class Dialogue {

    private final List<Line> lines = new ArrayList<>();
    private Message previousMessage = null;
    private int chance = 100;

    public Message getPreviousMessage() {
        return previousMessage;
    }

    public void setPreviousMessage(Message previousMessage) {
        this.previousMessage = previousMessage;
    }

    public int getChance() {
        return chance;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void addLine(Club actor, GuildMessageChannelUnion actorChannel, String line,
                        double lineSpeed, GuildSticker sticker, Emoji reaction) {

        lines.add(new Line(actor, actorChannel, line, lineSpeed, sticker, reaction));
    }

    public record Line(Club actor, GuildMessageChannelUnion actorChannel, String line,
                       double lineSpeed, GuildSticker sticker, Emoji reaction) {}
}
