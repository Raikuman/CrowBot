package com.raikuman.troubleclub.tamagopet.event.normal;

import com.raikuman.botutilities.invocation.type.SelectComponent;
import com.raikuman.troubleclub.tamagopet.TamagopetEnum;
import com.raikuman.troubleclub.tamagopet.TamagopetManager;
import com.raikuman.troubleclub.tamagopet.event.TamagopetEvent;
import com.raikuman.troubleclub.tamagopet.event.TamagopetSelectBuilder;
import com.raikuman.troubleclub.tamagopet.image.TamagopetImage;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.util.List;
import java.util.Random;

public class TamagopetFeed extends TamagopetEvent {

    private final List<String> titles = List.of(
        "Ben's in the mood for a little snack!",
        "Looks like Ben's in need of some food!",
        "Ben seems a bit peckish!",
        "Ben's tummy is grumbling!",
        "Ben wants a treat!"
    );

    private final List<String> descriptions = List.of(
        "Offer a delicacy of choice!",
        "Surely you have something you can give Ben?",
        "Tasty food only for the best of Bens.",
        "Anything ya got on you?"
    );

    private final Random random = new Random();
    private final List<SelectComponent> selects;
    private static final String FOOD_PATH = "food" + File.separator;

    public TamagopetFeed(TamagopetManager tamagopetManager) {
        super(tamagopetManager);

        selects = List.of(
            TamagopetSelectBuilder.setup("Pizza", tamagopetManager, TamagopetEnum.FOOD)
                .setImposeImage(FOOD_PATH + "pizza.png")
                .setHappiness(17)
                .setTitles(List.of(
                    "Ben takes a slice of pizza!",
                    "Ben went to Costco!",
                    "Ben's ready for some cheese!",
                    "Ben loves a pizza!",
                    "Ben takes your pizza!"))
                .setDescriptions(List.of(
                    "Ben's greasy grubby hands shakes with joy.",
                    "Ben enjoys a nice slice!",
                    "Ben seems content with his share of the pizza."))
                .build(),
            TamagopetSelectBuilder.setup("Soda", tamagopetManager, TamagopetEnum.FOOD)
                .setImposeImage(FOOD_PATH + "soda.png")
                .setHappiness(12)
                .setTitles(List.of(
                    "Ben takes a sip of soda!",
                    "Ben went to 7-Eleven!",
                    "Ben took a trip to McDonalds!",
                    "Ben loves a fizzy drink!"))
                .setDescriptions(List.of(
                    "Ben enjoys a carbonated beverage."))
                .build()
        );
    }

    @Override
    public List<SelectComponent> getSelects() {
        return selects;
    }

    @Override
    public String selectMenuPlaceholder() {
        return "Foods";
    }

    @Override
    public String title() {
        return titles.get(random.nextInt(titles.size()));
    }

    @Override
    public String description() {
        return descriptions.get(random.nextInt(descriptions.size()));
    }

    @Override
    public FileUpload getImage() {
        return new TamagopetImage("benhungry.png").retrieveAsFile();
    }
}
