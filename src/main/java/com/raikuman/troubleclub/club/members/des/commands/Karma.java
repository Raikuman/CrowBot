package com.raikuman.troubleclub.club.members.des.commands;

import com.raikuman.botutilities.commands.manager.CategoryInterface;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.DateAndTime;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.troubleclub.club.category.FunCategory;
import com.raikuman.troubleclub.club.config.yamboard.YamboardDB;
import com.raikuman.troubleclub.club.members.des.yamboard.KarmaObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles showing the user's karma for the yamboard
 *
 * @version 1.0 2023-07-03
 * @since 1.0
 */
public class Karma implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		KarmaObject karma;
		Member targetMember = null;

		boolean sendKarma = true;
		if (ctx.getArgs().size() == 0) {
			targetMember = ctx.getEventMember();
		} else if (ctx.getArgs().size() == 1) {
			if (ctx.getArgs().get(0).length() > 1) {
				if (ctx.getArgs().get(0).substring(0, 2).equalsIgnoreCase("<@") &&
					ctx.getEvent().getMessage().getMentions().getMembers().size() == 1) {
					targetMember = ctx.getEvent().getMessage().getMentions().getMembers().get(0);
				} else {
					sendKarma = false;
				}
			} else {
				sendKarma = false;
			}
		} else {
			sendKarma = false;
		}

		if (!sendKarma || targetMember == null) {
			MessageResources.timedMessage(
				"You must provide a valid argument for this command: `" + getUsage() + "`",
				ctx.getChannel().asTextChannel(),
				5
			);
			return;
		}

		karma = YamboardDB.getKarma(targetMember.getIdLong());
		if (karma == null) {
			karma = new KarmaObject(0, 0, 0);
		}

		EmbedBuilder builder = new EmbedBuilder()
			.setAuthor(targetMember.getEffectiveName() + "'s Karma", null,
				targetMember.getEffectiveAvatarUrl())
			.setColor(RandomColor.getRandomColor())
			.setFooter(DateAndTime.getDate() + " " + DateAndTime.getTime(), targetMember.getEffectiveAvatarUrl())
			.addField("Total Upvotes", String.valueOf(karma.getUpvotes()), true)
			.addField("Total Downvotes", String.valueOf(karma.getDownvotes()), true)
			.addField("Overall Ratio", String.valueOf(karma.getRatioPercent()), true)
			.addField("Posts", String.valueOf(karma.getPosts()), true);
		StringBuilder descriptionBuilder = builder.getDescriptionBuilder();

		// Ratio
		descriptionBuilder
			.append("You have a karma ratio of ***")
			.append(karma.getUpvotes())
			.append("/")
			.append(karma.getUpvotes() + karma.getDownvotes())
			.append("***\n\n");

		String upvotePostRatio, downvotePostRatio;
		if (karma.getPosts() == 0) {
			upvotePostRatio = "-%";
			downvotePostRatio = "-%";
		} else {
			DecimalFormat df = new DecimalFormat("#%");
			upvotePostRatio = df.format((double) (karma.getUpvotes() / karma.getPosts()));
			downvotePostRatio = df.format((double) (karma.getDownvotes() / karma.getPosts()));
		}

		// Upvote ratio
		descriptionBuilder
			.append("You have an upvote to post ratio of ***")
			.append(upvotePostRatio)
			.append("***\n\n");

		// Downvote ratio
		descriptionBuilder
			.append("You have a downvote to post ratio of ***")
			.append(downvotePostRatio)
			.append("***");

		ctx.getChannel().sendMessageEmbeds(builder.build()).queue();
		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "karma";
	}

	@Override
	public String getUsage() {
		return "(<@someone>)";
	}

	@Override
	public String getDescription() {
		return "Check your (or someone else) current karma score!";
	}

	@Override
	public List<String> getAliases() {
		return new ArrayList<>();
	}

	@Override
	public CategoryInterface getCategory() {
		return new FunCategory();
	}
}
