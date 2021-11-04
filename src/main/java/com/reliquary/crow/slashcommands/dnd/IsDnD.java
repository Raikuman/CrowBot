package com.reliquary.crow.slashcommands.dnd;

import com.reliquary.crow.resources.RandomClasses.RandomColor;
import com.reliquary.crow.slashcommands.manager.SlashContext;
import com.reliquary.crow.slashcommands.manager.SlashInterface;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Random;


public class IsDnD implements SlashInterface {
	@Override
	public void handle(SlashContext ctx) {

		Random rand = new Random();
		int currentNum = rand.nextInt(5) + 1;
		String string = "";

		switch(currentNum) {
			case 1:
				string = "Yes";
				break;

			case 2:
				string = "Sí";
				break;

			case 3:
				string = "はい";
				break;

			case 4:
				string = "是";
				break;

			case 5:
				string = "예";
				break;


		}

		// Playlist embed
		EmbedBuilder builder = new EmbedBuilder()
			.setAuthor(ctx.getEvent().getUser().getName(), null, ctx.getEvent().getUser().getAvatarUrl())
			.setTitle("Is D&D Friday: " + string)
			.setColor(RandomColor.getRandomColor());

		if (currentNum != 1) {
			builder.addField("Translation:", "Yes", true);
		}

		System.out.println("What the");

		ctx.getEvent().replyEmbeds(builder.build()).setEphemeral(false).queue();
	}

	@Override
	public String getInvoke() {
		return "isdnd";
	}
}
