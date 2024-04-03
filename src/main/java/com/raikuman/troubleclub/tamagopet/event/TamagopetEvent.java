package com.raikuman.troubleclub.tamagopet.event;

import com.raikuman.botutilities.invocation.type.ButtonComponent;
import com.raikuman.botutilities.invocation.type.SelectComponent;
import com.raikuman.troubleclub.tamagopet.TamagopetData;
import com.raikuman.troubleclub.tamagopet.TamagopetManager;
import net.dv8tion.jda.api.utils.FileUpload;

import java.util.ArrayList;
import java.util.List;

public abstract class TamagopetEvent {

    public final TamagopetManager tamagopetManager;

    protected TamagopetEvent(TamagopetManager tamagopetManager) {
        this.tamagopetManager = tamagopetManager;
    }

    public List<ButtonComponent> getButtons() {
        return new ArrayList<>();
    }

    public List<SelectComponent> getSelects() {
        return new ArrayList<>();
    }

    public String selectMenuPlaceholder() {
        return "";
    }

    public abstract String title();
    public abstract String description();
    public abstract FileUpload getImage();
}
