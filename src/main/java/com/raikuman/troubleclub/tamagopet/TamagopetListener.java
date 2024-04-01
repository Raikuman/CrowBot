package com.raikuman.troubleclub.tamagopet;

import com.raikuman.botutilities.defaults.database.DefaultDatabaseHandler;
import com.raikuman.botutilities.invocation.component.ComponentHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class TamagopetListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(TamagopetListener.class);
    private final ExecutorService executor;
    private final TamagopetManager tamagopetManager;

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
                Member member = event.getMember();
                if (member == null) {
                    return;
                }

                if (event.getMessage().getContentRaw().equals(DefaultDatabaseHandler.getPrefix(event.getGuild()) +
                    "event") && member.getPermissions().contains(Permission.ADMINISTRATOR)) {
                    tamagopetManager.handleTamagopet(event.getMessage(), true);
                    return;
                }

                tamagopetManager.handleTamagopet(event.getMessage(), false);
            }
        });
    }
}
