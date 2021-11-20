package com.reliquary.crow.commands.dnd;

import com.reliquary.crow.commands.manager.CommandContext;
import com.reliquary.crow.commands.manager.CommandInterface;
import com.reliquary.crow.resources.other.DateAndTime;
import com.reliquary.crow.resources.other.RandomColor;
import net.dv8tion.jda.api.EmbedBuilder;

import java.security.SecureRandom;

public class Dice implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {

		// Delete command message
		ctx.getMessage().delete().queue();

		if (ctx.getArgs().isEmpty()) {
			generateDiceEmbed(ctx, 1, 20);
		}

		if (ctx.getArgs().get(0).equals("..")) {
			EmbedBuilder builder = new EmbedBuilder()
				.setTitle(ctx.getAuthor().getName() + " rolled " + 1 + "d" + 20)
				.setColor(RandomColor.getRandomColor())
				.setFooter(DateAndTime.getDate() + " " + DateAndTime.getTime(), ctx.getAuthor().getAvatarUrl());
			StringBuilder descriptionBuilder = builder.getDescriptionBuilder();

			// Appends the rolls to the roll embed
			descriptionBuilder
				.append("```md\n")
				.append("<Total: ")
				.append(1)
				.append("> < Rolls: ")
				.append(1)
				.append(">")
				.append("```");

			// Send message
			ctx.getChannel().sendMessageEmbeds(builder.build())
				.queue();


		}

		// Input validation & overflow protection
		try {
			if ((ctx.getArgs().size() == 2) &&
				(Integer.parseInt(ctx.getArgs().get(0)) <= 100) &&
				(Integer.parseInt(ctx.getArgs().get(1)) <= 100)) {

				generateDiceEmbed(
					ctx,
					Integer.parseInt(ctx.getArgs().get(0)),
					Integer.parseInt(ctx.getArgs().get(1)));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getInvoke() {
		return "dice";
	}

	@Override
	public String getHelp() {
		return "Rolls a dice (up to 100 dice at once)";
	}

	@Override
	public String getUsage() {
		return "null";
	}

	@Override
	public String getCategory() {
		return "dnd";
	}

	public void generateDiceEmbed(CommandContext ctx, int numDice, int diceRange) {
		EmbedBuilder builder = new EmbedBuilder()
			.setTitle(ctx.getAuthor().getName() + " rolled " + numDice + "d" + diceRange)
			.setColor(RandomColor.getRandomColor())
			.setFooter(DateAndTime.getDate() + " " + DateAndTime.getTime(), ctx.getAuthor().getAvatarUrl());
		StringBuilder descriptionBuilder = builder.getDescriptionBuilder();

		// Gets random numbers
		StringBuilder diceRolls = new StringBuilder();
		int diceTotal = 0, currentNum;
		SecureRandom rand = new SecureRandom();

		for (int i = 0; i < numDice; i++) {
			currentNum = rand.nextInt(diceRange) + 1;
			diceTotal += currentNum;
			diceRolls
				.append(currentNum)
				.append(" ");
			rand.reseed();
		}

		// Appends the rolls to the roll embed
		descriptionBuilder
			.append("```md\n")
			.append("<Total: ")
			.append(diceTotal)
			.append("> < Rolls: ")
			.append(diceRolls)
			.append(">")
			.append("```");

		// Send message
		ctx.getChannel().sendMessageEmbeds(builder.build())
			.queue();

		// Delete command message
		ctx.getMessage().delete().queue();
	}
}
