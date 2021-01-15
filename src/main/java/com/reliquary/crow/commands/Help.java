package com.reliquary.crow.commands;

import com.reliquary.crow.commands.manager.CommandContext;
import com.reliquary.crow.commands.manager.CommandInterface;
import com.reliquary.crow.commands.manager.CommandManager;
import com.reliquary.crow.resources.RandomClasses.RandomColor;
import com.reliquary.crow.resources.configs.ConfigHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Help implements CommandInterface {

	private final CommandManager manager;

	// Create array of categories
	ArrayList<Category> categories = new ArrayList<>(List.of(
		new Category("basic", ":egg:"),
		new Category("fun", ":joystick:"),
		new Category("dnd", ":mage:"),
		new Category("music", ":notes:"),
		new Category("settings", ":gear:"),
		new Category("admin", ":vertical_traffic_light:")
	));

	public Help(CommandManager manager) {
		this.manager = manager;
	}

	@Override
	public void handle(CommandContext ctx) {

		List<String> args = ctx.getArgs();
		TextChannel channel = ctx.getChannel();

		// Output help embed for all categories (no args)
		if (args.isEmpty()) {
			generateCategoryListEmbed(channel);
			return;
		}

		// Check arg, search if category
		for (Category category : categories) {
			if (category.getName().toLowerCase().contains(args.get(0))) {
				generateCategoryCommandsEmbed(channel, args.get(0));
				return;
			}
		}
		// Check if arg is a command
		CommandInterface command = manager.getCommand(args.get(0));
		if (command != null) {
			generateCommandEmbed(channel, command);
			return;
		}

		// If args doesn't match category/command name
		generateErrorEmbed(channel, args.get(0));
	}

	@Override
	public String getInvoke() {
		return "help";
	}

	@Override
	public String getHelp() {
		return "Shows a list of commands!";
	}

	@Override
	public String getUsage() {
		return "<(category or command name)>";
	}

	@Override
	public String getCategory() {
		return "help";
	}

	/*
	generateCategoryListEmbed
	Generates a list of categories
	 */
	private void generateCategoryListEmbed(TextChannel channel) {

		EmbedBuilder builder = new EmbedBuilder()
			.setTitle("Command Categories")
			.setColor(RandomColor.getRandomColor());
		StringBuilder descriptionBuilder =  builder.getDescriptionBuilder();

		descriptionBuilder
			.append("Use `")
			.append(ConfigHandler.loadConfigSetting("botSettings", "prefix"))
			.append("help <category>` to view commands in a category!\n\n");

		// Append each category & info to description
		for (Category category : categories) {
			// Append emoji
			descriptionBuilder
				.append(category.getEmoji())
				.append(" ");

			// Uppercase first char in category, append
			String categoryName = category.getName().substring(0, 1).toUpperCase() +
				category.getName().substring(1).toLowerCase();

			descriptionBuilder
				.append("**")
				.append(categoryName)
				.append("**\n");

			// Change plurality of word 'command' for categories
			String strCommand;
			if (getNumOfCommands(category.getName()) == 1)
				strCommand = "command";
			else
				strCommand = "commands";

			descriptionBuilder
				.append("*")
				.append(getNumOfCommands(category.getName()))
				.append(" ")
				.append(strCommand)
				.append("*\n\n");
		}

		// Send message
		channel.sendMessage(builder.build())
			.queue();
	}

	/*
	generateCategoryCommandsEmbed
	Generates a list of commands under a category
	 */
	private void generateCategoryCommandsEmbed(TextChannel channel, String category) {

		// Uppercase first char in category
		String categoryName = category.substring(0, 1).toUpperCase() +
			category.substring(1).toLowerCase();

		EmbedBuilder builder = new EmbedBuilder()
			.setTitle(categoryName + " Commands")
			.setColor(RandomColor.getRandomColor());
		StringBuilder descriptionBuilder =  builder.getDescriptionBuilder();

		// Append commands in category to description
		for (CommandInterface command : manager.getCommands())
			if (command.getCategory().equals(category)) {
				descriptionBuilder
					.append("```asciidoc\n")
					.append(ConfigHandler.loadConfigSetting("botSettings", "prefix"))
					.append(command.getInvoke());

				if (!command.getUsage().isEmpty())
					descriptionBuilder
						.append(" ")
						.append(command.getUsage());

				descriptionBuilder
					.append(" :: ")
					.append(command.getHelp())
					.append("```\n");
			}

		// Send message
		channel.sendMessage(builder.build())
			.queue();
	}

	/*
	generateCommandEmbed
	Generates the help text of a command
	 */
	private void generateCommandEmbed(TextChannel channel, CommandInterface command) {

		// Uppercase first char in command
		String commandName = command.getInvoke().substring(0, 1).toUpperCase() +
			command.getInvoke().substring(1).toLowerCase();

		EmbedBuilder builder = new EmbedBuilder()
			.setTitle(commandName + " Commands")
			.setColor(RandomColor.getRandomColor());
		StringBuilder descriptionBuilder =  builder.getDescriptionBuilder();

		descriptionBuilder
			.append("```asciidoc\n")
			.append(ConfigHandler.loadConfigSetting("botSettings", "prefix"))
			.append(command.getInvoke());

		if (!command.getUsage().isEmpty())
			descriptionBuilder
				.append(" ")
				.append(command.getUsage());

		descriptionBuilder
			.append(" :: ")
			.append(command.getHelp())
			.append("```\n");

		// Send message
		channel.sendMessage(builder.build())
			.queue();
	}

	/*
	generateErrorEmbed
	Generates an error on incorrect args
	 */
	private void generateErrorEmbed(TextChannel channel, String arg) {

		EmbedBuilder builder = new EmbedBuilder()
			.setTitle("Nothing found!")
			.setColor(RandomColor.getRandomColor());
		StringBuilder descriptionBuilder =  builder.getDescriptionBuilder();

		descriptionBuilder
			.append("The command `")
			.append(arg)
			.append("` does not exist\n")
			.append("Use `")
			.append(ConfigHandler.loadConfigSetting("botSettings", "prefix"))
			.append(getInvoke())
			.append("` for a list of commands");

		// Send message
		channel.sendMessage(builder.build())
			.delay(Duration.ofSeconds(10))
			.flatMap(Message::delete)
			.queue();
	}

	/*
	getNumOfCommands
	Returns number of commands in a category
	 */
	private int getNumOfCommands(String category) {

		int num = 0;

		for (CommandInterface command : manager.getCommands())
			if (command.getCategory().equals(category))
				num++;

		return num;
	}
}

class Category {

	String name, emoji;

	// Constructor
	public Category(String name, String emoji) {
		this.name = name;
		this.emoji = emoji;
	}

	// Accessors
	public String getName() {
		return name;
	}

	public String getEmoji() {
		return emoji;
	}
}
