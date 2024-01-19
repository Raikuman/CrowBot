package com.raikuman.troubleclub.yamboard;

import com.raikuman.botutilities.config.ConfigData;
import com.raikuman.troubleclub.yamboard.buttons.YamboardButtonManager;
import com.raikuman.troubleclub.yamboard.config.YamboardConfig;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class YamboardListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(YamboardListener.class);
    private final ExecutorService executor;
    private Emoji yamEmoji;
    private MessageChannelUnion yamChannel, listenChannel;
    private final YamboardButtonManager buttonManager = new YamboardButtonManager();
    private YamboardManager yamboardManager;

    public YamboardListener(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("{}" + YamboardListener.class.getName() + " is initialized",
            event.getJDA().getSelfUser().getEffectiveName());

        ConfigData yamConfig = new ConfigData(new YamboardConfig());

        yamChannel = event.getJDA().getChannelById(MessageChannelUnion.class,
            yamConfig.getConfig("yamchannel"));

        listenChannel = event.getJDA().getChannelById(MessageChannelUnion.class,
            yamConfig.getConfig("listenchannel"));

        yamboardManager = new YamboardManager(buttonManager, yamConfig, yamChannel, listenChannel);
        yamEmoji = yamboardManager.getEmojis().get(0);
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        executor.submit(() -> {
            synchronized (this) {
                if (!event.getChannel().equals(listenChannel)) return;
                if (event.getUser() == null) return;
                if (event.getUser().isBot()) return;

                if (yamEmoji.equals(event.getEmoji())) {
                    Message message = event.retrieveMessage().complete();
                    if (message == null) return;

                    yamboardManager.handleYam(
                        event.getUser(),
                        message,
                        true);
                }
            }
        });
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        executor.submit(() -> {
            synchronized (this) {
                if (!event.getChannel().equals(listenChannel)) return;
                if (event.getUser() == null) return;
                if (event.getUser().isBot()) return;

                if (yamEmoji.equals(event.getEmoji())) {
                    Message message = event.retrieveMessage().complete();
                    if (message == null) return;

                    yamboardManager.handleYam(
                        event.getUser(),
                        message,
                        false);
                }
            }
        });
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        executor.submit(() -> {
            synchronized (this) {
                buttonManager.handleEvent(event);
            }
        });
    }
}
