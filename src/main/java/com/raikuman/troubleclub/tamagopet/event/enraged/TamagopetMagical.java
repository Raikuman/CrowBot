package com.raikuman.troubleclub.tamagopet.event.enraged;

import com.raikuman.troubleclub.tamagopet.TamagopetManager;
import com.raikuman.troubleclub.tamagopet.event.TamagopetEvent;
import com.raikuman.troubleclub.tamagopet.image.TamagopetImage;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.util.List;
import java.util.Random;

public class TamagopetMagical extends TamagopetEvent {

    private final List<String> titles = List.of(
        "Bengrammaz's barrier is down!",
        "Bengrammaz seems open to a spell!",
        "Bengrammaz's resistances are low!"
    );

    private final List<String> descriptions = List.of(
        "Fire a spell!",
        "Cast something!"
    );

    private final Random random = new Random();
    private static final String MAGICAL_PATH = "magical" + File.separator;

    public TamagopetMagical(TamagopetManager tamagopetManager) {
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
        return new TamagopetImage("benbarrier.png").retrieveAsFile();
    }
}