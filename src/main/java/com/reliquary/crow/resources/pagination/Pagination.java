package com.reliquary.crow.resources.pagination;

import com.reliquary.crow.resources.other.RandomColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class provides buttons and embeds for pagination
 *
 * @version 1.3 2021-23-12
 * @since 1.0
 */
public class Pagination {

	/**
	 * This method provides a list of button components for pagination
	 * @param invoke Provides invoke string for pagination command
	 * @param userId Provides invoke author's id to identify button
	 * @return Returns a list of button components
	 */
	public static List<Component> provideButtons(String invoke, String userId) {

		List<Component> componentList = new ArrayList<>();

		componentList.add(
			Button.secondary(userId + ":" + invoke + "left",  Emoji.fromMarkdown("⬅️")).asDisabled()
		);

		componentList.add(
			Button.secondary(userId + ":" + invoke + "right",  Emoji.fromMarkdown("➡️"))
		);

		return componentList;
	}

	public static Component provideHomeButton(String invoke, String userId) {
		return Button.secondary(userId + ":" + invoke + "home", Emoji.fromMarkdown("\uD83C\uDFE0"));
	}

	/**
	 * This method builds the EmbedBuilder list for pagination
	 * @param invoke Provides the invoke string to name the embeds
	 * @param stringList Provides the list of strings to construct the embeds
	 * @param itemsPerPage Provides the number of items (strings) that should be on a single embed page
	 * @return Returns the list of embed builders
	 */
	public static List<EmbedBuilder> buildEmbedList(String invoke, List<String> stringList,
		int itemsPerPage, String avatarUrl) {

		int numPages = (int) Math.ceil(stringList.size() / (double) itemsPerPage);

		List<EmbedBuilder> embedBuilderList = new ArrayList<>();
		EmbedBuilder builder = new EmbedBuilder();
		StringBuilder descriptionBuilder;

		int stringCount = 0;

		for (int i = 0; i < numPages; i++) {

			builder
				.setAuthor(invoke.substring(0, 1).toUpperCase() + invoke.substring(1), null, avatarUrl)
				.setTitle("Page " + (i + 1) + "/" + numPages)
				.setColor(RandomColor.getRandomColor());

			descriptionBuilder = builder.getDescriptionBuilder();

			for (int j = 0; j < itemsPerPage; j++) {
				if (stringCount < stringList.size()) {

					descriptionBuilder
						.append(stringList.get(stringCount))
						.append("\n\n");
				}
				stringCount++;
			}

			embedBuilderList.add(builder);
			builder = new EmbedBuilder();
		}

		return embedBuilderList;
	}
}