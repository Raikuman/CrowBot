package com.reliquary.crow.invokes.slashcommands.help.resources;

import com.reliquary.crow.managers.componentmanagers.selectionmenus.SelectInterface;
import com.reliquary.crow.invokes.slashcommands.help.selectionmenus.*;
import com.reliquary.crow.resources.other.RandomColor;
import com.reliquary.crow.slashcommands.help.selectionmenus.*;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * This class has methods for the help command to help build the embeds and provide information to the help
 * command
 *
 * @version 1.0 2021-09-12
 * @since 1.0
 */
public class HelpResources {

	private static final List<HelpCategory> categories = Arrays.asList(
		new HelpCategory("basic", ":egg:"),
		new HelpCategory("fun", ":joystick:"),
		new HelpCategory("dnd", ":mage:"),
		new HelpCategory("music", ":notes:"),
		new HelpCategory("settings", ":gear:")
	);

	private static final List<SelectInterface> selectionMenuInterfaces = Arrays.asList(
		new HelpHome(),
		new HelpBasic(),
		new HelpFun(),
		new HelpDnD(),
		new HelpMusic(),
		new HelpSettings()
	);

	/**
	 * This method returns the list of HelpCategories
	 * @return Return HelpCategory list
	 */
	public static List<HelpCategory> getCategories() {
		return categories;
	}

	/**
	 * This method returns the list of SelectInterfaces
	 * @return Return SelectInterface list
	 */
	public static List<SelectInterface> getSelectionMenuInterfaces() {
		return selectionMenuInterfaces;
	}

	/**
	 * This method builds a list of strings of a given category
	 * @param category Provides the category string
	 * @return Returns a list of strings of all invokes in a category
	 */
	public static List<String> buildCategoryStrings(String category) {
		return new HelpStringBuilder().getCategoryStrings(category);
	}

	/**
	 * This method gets the number of invokes of a given category
	 * @param category Provides the category string
	 * @return Returns the number of invokes in a category
	 */
	public static int getNumCategoryInvokes(String category) {
		return new HelpCategoryProvider().getNumCategoryInvokes(category);
	}

	/**
	 * This method provides the embed for the categories of the help command
	 * @param category Provides the category string
	 * @return Returns the EmbedBuilder of categories
	 */
	public static EmbedBuilder provideHelpEmbed(String category) {

		EmbedBuilder builder = new EmbedBuilder()
			.setTitle(category + " Commands")
			.setColor(RandomColor.getRandomColor());
		StringBuilder descriptionBuilder = builder.getDescriptionBuilder();

		descriptionBuilder
			.append("```asciidoc\n");

		for (String string : HelpResources.buildCategoryStrings(category)) {
			descriptionBuilder
				.append(string)
				.append("\n\n");
		}

		descriptionBuilder
			.append("```");

		return builder;
	}

	/**
	 * This method provides the base embed for the help command
	 * @return Returns the EmbedBuilder of the base help command
	 */
	public static EmbedBuilder provideHelpHomeEmbed() {

		EmbedBuilder builder = new EmbedBuilder()
			.setTitle("Command Categories")
			.setColor(RandomColor.getRandomColor());
		StringBuilder descriptionBuilder = builder.getDescriptionBuilder();

		descriptionBuilder
			.append("Use the buttons to see commands under a category!\n\n ");

		for (HelpCategory category : getCategories()) {

			descriptionBuilder
				.append(category.getEmoji())
				.append(" **")
				.append(category.getName().substring(0, 1).toUpperCase())
				.append(category.getName().substring(1).toLowerCase())
				.append("**\n*")
				.append(getNumCategoryInvokes(category.getName()));

			// Change plurality of word 'command' for categories
			if (getNumCategoryInvokes(category.getName()) == 1)
				descriptionBuilder
					.append(" command*");
			else
				descriptionBuilder
					.append(" commands*");

			descriptionBuilder
				.append("\n\n");
		}

		return builder;
	}
}