package com.raikuman.troubleclub.yamboard.buttons;

import com.raikuman.botutilities.invocation.type.ButtonComponent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class YamboardButtonManager {

    private static final Logger logger = LoggerFactory.getLogger(YamboardButtonManager.class);
    public static final int MAX_ENTRIES = 2;
    private final LinkedHashMap<Message, HashMap<String, ButtonComponent>> interactions = new LinkedHashMap<>(MAX_ENTRIES) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Message, HashMap<String, ButtonComponent>> eldest) {
            if (size() > MAX_ENTRIES) {
                eldest.getKey().editMessageComponents().queue();
                this.remove(eldest.getKey());
                return true;
            }

            return false;
        }
    };

    public LinkedHashMap<Message, HashMap<String, ButtonComponent>> getInteractions() {
        return interactions;
    }

    public void addButtons(Message message, List<ButtonComponent> buttons) {
        HashMap<String, ButtonComponent> buttonMap = new HashMap<>();
        for (ButtonComponent button : buttons) {
            buttonMap.put(button.getInvoke(), button);
        }

        this.interactions.put(message, buttonMap);
    }

    public void handleEvent(ButtonInteractionEvent event) {
        // Force handling in guild only
        if (!event.isFromGuild()) {
            return;
        }

        // Split component id to get author and invocation
        String[] id = event.getComponentId().split(":");
        if (id.length != 2) {
            logger.error("Invalid button component id: " + event.getComponentId());
            return;
        }

        // Retrieve button
        HashMap<String, ButtonComponent> components = interactions.get(event.getMessage());
        if (components == null) {
            return;
        }

        ButtonComponent button = components.get(id[1]);
        if (button == null) {
            logger.error("Invalid button invocation: " + id[1]);
            return;
        }

        // Handle button
        button.handle(event);
    }

    public void removeButtons(Message message) {
        interactions.remove(message);
    }
}
