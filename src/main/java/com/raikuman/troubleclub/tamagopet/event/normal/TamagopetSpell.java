package com.raikuman.troubleclub.tamagopet.event.normal;

import com.raikuman.troubleclub.tamagopet.TamagopetManager;
import com.raikuman.troubleclub.tamagopet.event.TamagopetEvent;
import com.raikuman.troubleclub.tamagopet.image.TamagopetImage;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.util.List;
import java.util.Random;

public class TamagopetSpell extends TamagopetEvent {

    private final List<String> titles = List.of(
        "Ben looks susceptible to a spell!",
        "Ben's looking the other way!",
        "Ben won't see it comin'!",
        "Hurry! Ben won't notice!",
        "Ben's preoccupied. Go for it!"
    );

    private final List<String> descriptions = List.of(
        "Get his ass!",
        "Take the shot!",
        "Try not to miss!",
        "Go for it!"
    );

    private final Random random = new Random();
    private static final String SPELL_PATH = "spell" + File.separator;

    public TamagopetSpell(TamagopetManager tamagopetManager) {
        super(tamagopetManager);
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
        return new TamagopetImage("benspell.png").retrieveAsFile();
    }
}
