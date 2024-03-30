package com.raikuman.troubleclub.invoke.category;

import com.raikuman.botutilities.invocation.Category;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.awt.*;

public class Tamagopet implements Category {

    public static final Color BEN_COLOR = Color.decode("#6b0c0f");

    @Override
    public String getCategory() {
        return "tamagopet";
    }

    @Override
    public Emoji getEmoji() {
        return Emoji.fromFormatted("\uD83D\uDC14");
    }
}
