package com.raikuman.troubleclub.interaction;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class InteractionListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(InteractionListener.class);
    private final ExecutorService executor;
    private final InteractionManager interactionManager;

    public InteractionListener(ExecutorService executor, InteractionManager interactionManager) {
        this.executor = executor;
        this.interactionManager = interactionManager;
    }

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("{}" + InteractionListener.class.getName() + " is initialized",
            event.getJDA().getSelfUser().getEffectiveName());
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        executor.submit(() -> {
            synchronized (this) {
                interactionManager.handleEvent(event);
            }
        });
    }
}
