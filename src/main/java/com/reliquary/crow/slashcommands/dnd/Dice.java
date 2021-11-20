package com.reliquary.crow.slashcommands.dnd;

import com.reliquary.crow.resources.other.DateAndTime;
import com.reliquary.crow.resources.other.RandomColor;
import com.reliquary.crow.slashcommands.manager.SlashContext;
import com.reliquary.crow.slashcommands.manager.SlashInterface;
import net.dv8tion.jda.api.EmbedBuilder;

import java.security.SecureRandom;

public class Dice implements SlashInterface {
	@Override
	public void handle(SlashContext ctx) {

		EmbedBuilder builder = new EmbedBuilder()
			.setTitle(ctx.getEvent().getUser().getName() + " rolled 1d20")
			.setColor(RandomColor.getRandomColor())
			.setFooter(DateAndTime.getDate() + " " + DateAndTime.getTime(), ctx.getEvent().getUser().getAvatarUrl());
		StringBuilder descriptionBuilder = builder.getDescriptionBuilder();

		// Gets random numbers
		StringBuilder diceRolls = new StringBuilder();
		int diceTotal = 0, currentNum;
		SecureRandom rand = new SecureRandom();

		for (int i = 0; i < 1; i++) {
			currentNum = rand.nextInt(20) + 1;
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

		ctx.getEvent().replyEmbeds(builder.build()).setEphemeral(false).queue();
	}

	@Override
	public String getInvoke() {
		return "dice";
	}
}
