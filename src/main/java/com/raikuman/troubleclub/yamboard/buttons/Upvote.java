package com.raikuman.troubleclub.yamboard.buttons;

import com.raikuman.botutilities.invocation.type.ButtonComponent;
import com.raikuman.troubleclub.yamboard.YamboardManager;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

public class Upvote implements ButtonComponent {

    private final YamboardManager yamboardManager;
    private final Emoji emoji;

    public Upvote(YamboardManager yamboardManager, Emoji emoji) {
        this.yamboardManager = yamboardManager;
        this.emoji = emoji;
    }

    @Override
    public void handle(ButtonInteractionEvent ctx) {
        yamboardManager.handleKarma(ctx.getUser(), ctx.getMessage(), false, true);
        ctx.deferEdit().queue();
    }

    @Override
    public String getInvoke() {
        return "upvote";
    }

    @Override
    public Emoji displayEmoji() {
        return emoji;
    }

    @Override
    public String displayLabel() {
        return null;
    }

    @Override
    public ButtonStyle buttonStyle() {
        return ButtonStyle.SUCCESS;
    }

    @Override
    public boolean ignoreAuthor() {
        return true;
    }
}
