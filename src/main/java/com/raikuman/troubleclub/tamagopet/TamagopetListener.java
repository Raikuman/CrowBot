package com.raikuman.troubleclub.tamagopet;

import com.raikuman.botutilities.invocation.component.ComponentHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class TamagopetListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(TamagopetListener.class);
    private final ExecutorService executor;
    private TamagopetManager tamagopetManager;

    public TamagopetListener(ExecutorService executor, ComponentHandler componentHandler) {
        this.executor = executor;
        tamagopetManager = new TamagopetManager(componentHandler);
    }

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("{}" + TamagopetListener.class.getName() + " is initialized",
            event.getJDA().getSelfUser().getEffectiveName());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        executor.submit(() -> {
            synchronized (this) {
                tamagopetManager.handleTamagopet(event.getMessage());
            }
        });
    }
}
