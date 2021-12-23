package com.reliquary.crow.resources.pagination.buttons;

import com.reliquary.crow.managers.componentmanagers.ComponentResources;
import com.reliquary.crow.managers.componentmanagers.buttons.ButtonContext;
import com.reliquary.crow.managers.componentmanagers.buttons.ButtonInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.Component;

import java.util.List;

/**
 * This class is part of the pagination implementation that handles the home page
 *
 * @version 1.2 2021-23-12
 * @since 1.1
 */
public class PageHome implements ButtonInterface {

	private final String invoke;

	public PageHome(String invoke) {
		this.invoke = invoke;
	}

	@Override
	public void handle(ButtonContext ctx) {

		ctx.getEvent().editMessageEmbeds(
			setEmbedBuilder(ctx).build()
		).setActionRows(ComponentResources.getActionRows(setComponents(ctx))).queue();
	}

	@Override
	public String getButtonId() {
		return invoke + "home";
	}

	@Override
	public Emoji getEmoji() {
		return Emoji.fromMarkdown("\uD83C\uDFE0");
	}

	@Override
	public String getLabel() {
		return null;
	}

	public EmbedBuilder setEmbedBuilder(ButtonContext ctx) {
		return null;
	}

	public List<Component> setComponents(ButtonContext ctx) {
		return null;
	}
}
