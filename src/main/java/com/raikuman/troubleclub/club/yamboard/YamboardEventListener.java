package com.raikuman.troubleclub.club.yamboard;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YamboardEventListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(YamboardEventListener.class);

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        logger.info("{}" + YamboardEventListener.class.getName() + " is initialized",
            event.getJDA().getSelfUser().getEffectiveName());
    }

    @Override
    public void onGenericMessageReaction(@NotNull GenericMessageReactionEvent event) {
        Member member = event.getMember();

        if (member == null)
            return;

        if (member.getUser().isBot())
            return;

        //YamboardManager.getInstance().handleReaction(event);
    }
}
