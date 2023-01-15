package com.raikuman.troubleclub.club.crow.commands.roleplaying;

import com.raikuman.botutilities.commands.manager.CategoryInterface;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.DateAndTime;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.troubleclub.club.category.RoleplayingCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.security.SecureRandom;
import java.util.List;

/**
 * Handles rolling a dice or multiple dice with different number of sides
 *
 * @version 1.0 2023-14-01
 * @since 1.0
 */
public class Dice implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel().asTextChannel();
		int numDice, maxSides;

		if (ctx.getArgs().isEmpty()) {
			numDice = 1;
			maxSides = 20;
		} else {
			if (ctx.getArgs().size() != 2) {
				MessageResources.timedMessage(
					"You must provide a valid argument for this command: `" + getUsage() + "`",
					channel,
					5
				);
				return;
			}

			try {
				numDice = Integer.parseInt(ctx.getArgs().get(0));
				maxSides = Integer.parseInt(ctx.getArgs().get(1));
			} catch (NumberFormatException e) {
				MessageResources.timedMessage(
					"You must provide a valid argument for this command: `" + getUsage() + "`",
					channel,
					5
				);
				return;
			}
		}

		ctx.getChannel().sendMessageEmbeds(generateDiceEmbed(ctx, numDice, maxSides).build()).queue();
		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "dice";
	}

	@Override
	public String getUsage() {
		return "(<# of dice>) (<# of sides>)";
	}

	@Override
	public String getDescription() {
		return "Roll a dice!";
	}

	@Override
	public List<String> getAliases() {
		return CommandInterface.super.getAliases();
	}

	@Override
	public CategoryInterface getCategory() {
		return new RoleplayingCategory();
	}

	/**
	 * Generate an EmbedBuilder with randomized dice rolls
	 * @param ctx The event context to get user information from
	 * @param numDice The number of dice to roll
	 * @param maxSides The number of sides on the dice
	 * @return The EmbedBuilder with the generated rolls and total
	 */
	private EmbedBuilder generateDiceEmbed(CommandContext ctx, int numDice, int maxSides) {
		StringBuilder diceRolls = new StringBuilder();
		SecureRandom rand = new SecureRandom();
		int diceTotal = 0, currentNum;

		// Generate rolls
		for (int i = 0; i < numDice; i++) {
			currentNum = rand.nextInt(maxSides) + 1;
			diceTotal += currentNum;
			diceRolls
				.append(currentNum)
				.append(" ");
			rand.reseed();
		}

		EmbedBuilder builder = new EmbedBuilder()
			.setTitle(ctx.getEventMember().getNickname() + " rolled " + numDice + "d" + maxSides)
			.setColor(RandomColor.getRandomColor())
			.setFooter(DateAndTime.getDate() + " " + DateAndTime.getTime(),
				ctx.getEventMember().getEffectiveAvatarUrl());
		StringBuilder descriptionBuilder = builder.getDescriptionBuilder();

		// Appends the rolls to the roll embed
		descriptionBuilder
			.append("```md\n")
			.append("<Total: ")
			.append(diceTotal)
			.append("> < Rolls: ")
			.append(diceRolls)
			.append(">")
			.append("```");

		return builder;
	}
}
