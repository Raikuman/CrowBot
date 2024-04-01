package com.raikuman.troubleclub.tamagopet.event;

import com.raikuman.botutilities.invocation.type.ButtonComponent;
import com.raikuman.botutilities.invocation.type.SelectComponent;
import com.raikuman.troubleclub.tamagopet.TamagopetData;
import net.dv8tion.jda.api.utils.FileUpload;

import java.util.ArrayList;
import java.util.List;

public interface TamagopetEvent {

    default List<ButtonComponent> getButtons(TamagopetData tamagopetData) {
        return new ArrayList<>();
    }

    default List<SelectComponent> getSelects(TamagopetData tamagopetData) {
        return new ArrayList<>();
    }

    default String selectMenuPlaceholder() {
        return "";
    }

    String title();
    String description();
    FileUpload getImage();
}
