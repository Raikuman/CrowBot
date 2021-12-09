package com.reliquary.crow.listeners.componentlisteners;

import com.reliquary.crow.managers.componentmanagers.selectionmenus.SelectManager;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;

public class SelectionMenuEventListener extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(SelectionMenuEventListener.class);
	private final SelectManager manager = new SelectManager();

	@Override
	public void onReady(@Nonnull ReadyEvent event) {
		logger.info("{} SelectionMenuListener is initialized", event.getJDA().getSelfUser().getAsTag());
	}

	@Override
	public void onSelectionMenu(SelectionMenuEvent event) {

		String value = getMenuValue(event.getValues());

		if (value == null)
			return;

		// Split component for checking
		String[] id = value.split(":");
		String authorId = id[0];
		String type = id[1];

		if (!authorId.equals(event.getUser().getId()) && !event.isFromGuild())
			return;

		manager.handle(event, type);
	}

	private String getMenuValue(List<String> values) {

		if (values.size() == 1)
			return values.get(0);
		else
			return null;
	}


}
