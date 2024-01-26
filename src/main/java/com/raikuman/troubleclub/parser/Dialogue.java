package com.raikuman.troubleclub.parser;

import com.raikuman.troubleclub.Club;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;

import java.util.ArrayList;
import java.util.List;

public class Dialogue {

    private final List<Line> lines = new ArrayList<>();
    private int chance = 100;

    public int getChance() {
        return chance;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void addLine(Club actor, GuildMessageChannelUnion actorChannel, String line, GuildSticker sticker,
                        Emoji reaction) {

        lines.add(new Line(actor, actorChannel, line, sticker, reaction));
    }

    public record Line(Club actor, GuildMessageChannelUnion actorChannel, String line, GuildSticker sticker,
                       Emoji reaction) {}
}
