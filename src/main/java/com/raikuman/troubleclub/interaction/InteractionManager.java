package com.raikuman.troubleclub.interaction;

import com.raikuman.troubleclub.Club;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class InteractionManager {

    private final ExecutorService executor;
    private final List<InteractionCache> interactionCaches;
    private HashMap<Club, JDA> clubMap;

    public InteractionManager(ExecutorService executor) {
        this.executor = executor;
        interactionCaches = InteractionLoader.loadInteractionCaches();
    }

    public void setClubMap(HashMap<Club, JDA> clubMap) {
        this.clubMap = clubMap;
    }

    public void handleEvent(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;


    }

    private Interaction getBestInteraction() {
        return null;
    }
}
