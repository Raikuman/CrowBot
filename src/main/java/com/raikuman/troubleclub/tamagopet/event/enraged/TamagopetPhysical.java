package com.raikuman.troubleclub.tamagopet.event.enraged;

import com.raikuman.troubleclub.tamagopet.TamagopetManager;
import com.raikuman.troubleclub.tamagopet.event.TamagopetEvent;
import com.raikuman.troubleclub.tamagopet.image.TamagopetImage;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.util.List;
import java.util.Random;

public class TamagopetPhysical extends TamagopetEvent {

    private final List<String> titles = List.of(
        "Bengrammaz's guard is down!",
        "Bengrammaz seems open to an attack!",
        "Bengrammaz looks like he can take a hit!"
    );

    private final List<String> descriptions = List.of(
        "Attack!",
        "Go on the offensive!"
    );

    private final Random random = new Random();
    private static final String PHYSICAL_PATH = "physical" + File.separator;

    public TamagopetPhysical(TamagopetManager tamagopetManager) {
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
        return new TamagopetImage("bendefense.png").retrieveAsFile();
    }
}
