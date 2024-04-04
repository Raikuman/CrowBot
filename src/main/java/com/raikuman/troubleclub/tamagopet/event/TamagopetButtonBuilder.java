package com.raikuman.troubleclub.tamagopet.event;

import com.raikuman.botutilities.invocation.type.ButtonComponent;
import com.raikuman.troubleclub.tamagopet.TamagopetDatabaseHandler;
import com.raikuman.troubleclub.tamagopet.TamagopetEnum;
import com.raikuman.troubleclub.tamagopet.TamagopetManager;
import com.raikuman.troubleclub.tamagopet.image.TamagopetImage;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.slf4j.helpers.CheckReturnValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TamagopetButtonBuilder {

    private final TamagopetManager tamagopetManager;
    private final TamagopetEnum tamagopetEnum;
    private final String label;
    private String baseImage, imposeImage;
    private int happiness, health, x, y;
    private List<String> titles, descriptions;
    private Emoji emoji;
    private ButtonStyle buttonStyle;

    public TamagopetButtonBuilder(String label, TamagopetManager tamagopetManager, TamagopetEnum tamagopetEnum) {
        this.label = label;
        this.tamagopetManager = tamagopetManager;
        this.tamagopetEnum = tamagopetEnum;
        this.baseImage = "";
        this.imposeImage = "";
        this.happiness = 0;
        this.health = 0;
        this.x = 0;
        this.y = 0;
        this.titles = new ArrayList<>();
        this.descriptions = new ArrayList<>();
        this.emoji = null;
        this.buttonStyle = null;
    }

    @CheckReturnValue
    public static TamagopetButtonBuilder setup(String label, TamagopetManager tamagopetManager, TamagopetEnum tamagopetEnum) {
        return new TamagopetButtonBuilder(label, tamagopetManager, tamagopetEnum);
    }

    @CheckReturnValue
    public TamagopetButtonBuilder setBaseImage(String baseImage) {
        this.baseImage = baseImage;
        return this;
    }

    @CheckReturnValue
    public TamagopetButtonBuilder setImposeImage(String imposeImage, int x, int y) {
        this.imposeImage = imposeImage;
        this.x = x;
        this.y = y;
        return this;
    }

    @CheckReturnValue
    public TamagopetButtonBuilder setImposeImage(String imposeImage) {
        this.imposeImage = imposeImage;
        return this;
    }

    @CheckReturnValue
    public TamagopetButtonBuilder setTitles(List<String> titles) {
        this.titles = titles;
        return this;
    }

    @CheckReturnValue
    public TamagopetButtonBuilder setDescriptions(List<String> descriptions) {
        this.descriptions = descriptions;
        return this;
    }

    @CheckReturnValue
    public TamagopetButtonBuilder setEmoji(Emoji emoji) {
        this.emoji = emoji;
        return this;
    }

    @CheckReturnValue
    public TamagopetButtonBuilder setButtonStyle(ButtonStyle buttonStyle) {
        this.buttonStyle = buttonStyle;
        return this;
    }

    @CheckReturnValue
    public TamagopetButtonBuilder setHappiness(int happiness) {
        this.happiness = happiness;
        return this;
    }

    @CheckReturnValue
    public TamagopetButtonBuilder setHealth(int health) {
        this.health = health;
        return this;
    }

    public ButtonComponent build() {
        TamagopetImage image = new TamagopetImage(baseImage);
        if (!imposeImage.isEmpty()) {
            image.superimpose(imposeImage, x, y);
        }

        return new ButtonComponent() {

            private final Random random = new Random();

            @Override
            public void handle(ButtonInteractionEvent ctx) {
                // Handle data
                tamagopetManager.addHappiness(ctx.getChannel(), happiness);
                tamagopetManager.reduceHealth(ctx.getChannel(), health);

                // Handle embed
                String title;
                if (titles.isEmpty()) {
                    title = "Tamagopet Title " + label;
                } else {
                    title = titles.get(random.nextInt(titles.size()));
                }

                String description;
                if (descriptions.isEmpty()) {
                    description = "Tamagopet Description " + label;
                } else {
                    description = descriptions.get(random.nextInt(descriptions.size()));
                }

                TamagopetImage.handleEmbedUpdate(
                    title,
                    description,
                    image,
                    ctx
                );

                // Handle data for user
                switch (tamagopetEnum) {
                    case FOOD -> TamagopetDatabaseHandler.addFoodEvent(ctx.getUser(), 1);
                    case BATH -> TamagopetDatabaseHandler.addBathEvent(ctx.getUser(), 1);
                    case SPELL -> TamagopetDatabaseHandler.addSpellEvent(ctx.getUser(), 1);
                    case PHYSICAL -> TamagopetDatabaseHandler.addPhysicalEvent(ctx.getUser(), 1);
                    case MAGICAL -> TamagopetDatabaseHandler.addMagicalEvent(ctx.getUser(), 1);
                }
            }

            @Override
            public String getInvoke() {
                return "tamagopetbutton" + label;
            }

            @Override
            public String displayLabel() {
                return label;
            }

            @Override
            public Emoji displayEmoji() {
                if (emoji != null) {
                    return emoji;
                } else {
                    return super.displayEmoji();
                }
            }

            @Override
            public ButtonStyle buttonStyle() {
                if (buttonStyle != null) {
                    return buttonStyle;
                } else {
                    return super.buttonStyle();
                }
            }
        };
    }
}
