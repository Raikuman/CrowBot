package com.raikuman.troubleclub.tamagopet.event.normal;

import com.raikuman.troubleclub.tamagopet.TamagopetManager;
import com.raikuman.troubleclub.tamagopet.event.TamagopetEvent;
import com.raikuman.troubleclub.tamagopet.image.TamagopetImage;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.util.List;
import java.util.Random;

public class TamagopetBath extends TamagopetEvent {

    private final List<String> titles = List.of(
        "Ben looks in need of a bath!",
        "Ben is covered in grime!",
        "Ben's been playing around in the mud!",
        "Ben's been up to some no good stuff!",
        "Ben is smelly!"
    );

    private final List<String> descriptions = List.of(
        "Why not give Ben a bath?",
        "Time for a bath!",
        "Maybe a little cleaning should do.",
        "Get Ben in that bath!"
    );

    private final Random random = new Random();
    private static final String BATH_PATH = "bath" + File.separator;

    public TamagopetBath(TamagopetManager tamagopetManager) {
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
        return new TamagopetImage("bendirty.png").retrieveAsFile();
    }
}
