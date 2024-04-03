package com.raikuman.troubleclub.tamagopet.event;

import com.raikuman.botutilities.invocation.type.SelectComponent;
import com.raikuman.troubleclub.tamagopet.TamagopetDatabaseHandler;
import com.raikuman.troubleclub.tamagopet.TamagopetEnum;
import com.raikuman.troubleclub.tamagopet.TamagopetManager;
import com.raikuman.troubleclub.tamagopet.image.TamagopetImage;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import org.slf4j.helpers.CheckReturnValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TamagopetSelectBuilder {

    private final TamagopetManager tamagopetManager;
    private final TamagopetEnum tamagopetEnum;
    private String label, baseImage, imposeImage;
    private int happiness, health, x, y;
    private List<String> titles, descriptions;

    private TamagopetSelectBuilder(String label, TamagopetManager tamagopetManager, TamagopetEnum tamagopetEnum) {
        this.tamagopetEnum = tamagopetEnum;
        this.tamagopetManager = tamagopetManager;
        this.label = label;
        this.baseImage = "";
        this.imposeImage = "";
        this.happiness = 0;
        this.health = 0;
        this.x = 0;
        this.y = 0;
        this.titles = new ArrayList<>();
        this.descriptions = new ArrayList<>();
    }

    @CheckReturnValue
    public static TamagopetSelectBuilder setup(String label, TamagopetManager tamagopetManager, TamagopetEnum tamagopetEnum) {
        return new TamagopetSelectBuilder(label, tamagopetManager, tamagopetEnum);
    }

    @CheckReturnValue
    public TamagopetSelectBuilder setBaseImage(String baseImage) {
        this.baseImage = baseImage;
        return this;
    }

    @CheckReturnValue
    public TamagopetSelectBuilder setImposeImage(String imposeImage, int x, int y) {
        this.imposeImage = imposeImage;
        this.x = x;
        this.y = y;
        return this;
    }

    @CheckReturnValue
    public TamagopetSelectBuilder setImposeImage(String imposeImage) {
        this.imposeImage = imposeImage;
        return this;
    }

    @CheckReturnValue
    public TamagopetSelectBuilder setTitles(List<String> titles) {
        this.titles = titles;
        return this;
    }

    @CheckReturnValue
    public TamagopetSelectBuilder setDescriptions(List<String> descriptions) {
        this.descriptions = descriptions;
        return this;
    }

    public TamagopetSelectBuilder setHappiness(int happiness) {
        this.happiness = happiness;
        return this;
    }

    public TamagopetSelectBuilder setHealth(int health) {
        this.health = health;
        return this;
    }

    public SelectComponent build() {
        TamagopetImage image = new TamagopetImage(baseImage);
        if (!imposeImage.isEmpty()) {
            image.superimpose(imposeImage, x, y);
        }

        return new SelectComponent() {

            private final Random random = new Random();

            @Override
            public void handle(StringSelectInteractionEvent ctx) {
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
                    case FOOD -> {
                        TamagopetDatabaseHandler.addFoodEvent(ctx.getUser(), 1);
                    }

                    case BATH -> {
                        TamagopetDatabaseHandler.addBathEvent(ctx.getUser(), 1);
                    }

                    case SPELL -> {
                        TamagopetDatabaseHandler.addSpellEvent(ctx.getUser(), 1);
                    }

                    case PHYSICAL -> {
                        TamagopetDatabaseHandler.addPhysicalEvent(ctx.getUser(), 1);
                    }

                    case MAGICAL -> {
                        TamagopetDatabaseHandler.addMagicalEvent(ctx.getUser(), 1);
                    }
                }
            }

            @Override
            public String getInvoke() {
                return "tamagopetselect" + label;
            }

            @Override
            public String displayLabel() {
                return label;
            }

            @Override
            public boolean ignoreAuthor() {
                return true;
            }
        };
    }
}
