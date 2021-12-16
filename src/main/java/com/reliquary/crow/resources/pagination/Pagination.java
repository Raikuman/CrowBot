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
 * @version 1.0 2021-16-12
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

		return Arrays.asList(
			Button.secondary(userId + ":" + invoke + "left",  Emoji.fromMarkdown("⬅️")).asDisabled(),
			Button.secondary(userId + ":" + invoke + "right",  Emoji.fromMarkdown("➡️"))
		);
	}

	/**
	 * This method builds the EmbedBuilder list for pagination
	 * @param invoke Provides the invoke string to name the embeds
	 * @param stringList Provides the list of strings to construct the embeds
	 * @param itemsPerPage Provides the number of items (strings) that should be on a single embed page
	 * @return Returns the list of embed builders
	 */
	public static List<EmbedBuilder> buildEmbedList(String invoke, List<String> stringList,
		int itemsPerPage) {

		int numPages = (int) Math.ceil(stringList.size() / (double) itemsPerPage);

		List<EmbedBuilder> embedBuilderList = new ArrayList<>();
		EmbedBuilder builder = new EmbedBuilder();
		StringBuilder descriptionBuilder;

		int stringCount = 0;

		for (int i = 0; i < numPages; i++) {

			builder
				.setAuthor(invoke.substring(0, 1).toUpperCase() + invoke.substring(1))
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