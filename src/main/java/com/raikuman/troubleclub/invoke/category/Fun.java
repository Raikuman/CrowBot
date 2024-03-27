package com.raikuman.troubleclub.invoke.category;

import com.raikuman.botutilities.invocation.Category;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.awt.*;

public class Fun implements Category {

    public static final Color FUN_COLOR = Color.decode("#fc0362");

    @Override
    public String getCategory() {
        return "fun";
    }

    @Override
    public Emoji getEmoji() {
        return Emoji.fromFormatted("\uD83C\uDF89");
    }
}
