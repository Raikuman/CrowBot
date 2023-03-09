package com.raikuman.troubleclub.club.members.des.yamboard;

import com.raikuman.botutilities.configs.ConfigIO;
import com.raikuman.botutilities.helpers.DateAndTime;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.troubleclub.club.config.yamboard.YamboardDB;
import com.raikuman.troubleclub.club.members.des.yamboard.buttons.Downvote;
import com.raikuman.troubleclub.club.members.des.yamboard.buttons.Upvote;
import kotlin.Pair;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles the YamBoard, a pinned-message board based around reactions. A target text channel is selected
 * to look for reactions. The reacted message will then be posted to a pinned text channel.
 *
 * @version 1.1 2023-07-03
 * @since 1.0
 */
public class YamboardUtilities {

	private static final Logger logger = LoggerFactory.getLogger(YamboardUtilities.class);

	/**
	 * Posts a message to the pinned text channel given information from the original user message
	 * @param originalMessage The message that was reacted to
	 * @param userChannel The text channel where the reaction took place
	 * @param postChannel The text channel where the pinned message will be posted to
	 * @return The id long and embed builder of the posted message
	 */
	public static Pair<Long, EmbedBuilder> postMessage(Message originalMessage, Member messageReactor,
		TextChannel userChannel, TextChannel postChannel) {
		// Check for original member in message
		Member originalMessager = originalMessage.getMember();
		if (originalMessager == null)
			return new Pair<>(-1L, null);

		// Build embed
		EmbedBuilder builder = new EmbedBuilder()
			.setTitle(originalMessager.getEffectiveName() + "'s original message", originalMessage.getJumpUrl())
			.setAuthor(messageReactor.getEffectiveName() + " yammed " + originalMessage.getMember().getEffectiveName() +
					"'s message!", null, messageReactor.getEffectiveAvatarUrl())
			.setColor(RandomColor.getRandomColor())
			.setFooter("#" + userChannel.getName() + " | " + DateAndTime.getDate() + " " + DateAndTime.getTime())
			.addField("Upvotes", "0", true)
			.addField("Downvotes", "0", true)
			.addField("Ratio", "-%", true);

		String url = checkForUrl(originalMessage.getContentRaw());
		if (originalMessage.getAttachments().isEmpty()) {
			if (url.isEmpty()) {
				builder.setDescription(originalMessage.getContentRaw());
			} else {
				if (!originalMessage.getEmbeds().isEmpty()) {
					MessageEmbed.Thumbnail thumbnail = originalMessage.getEmbeds().get(0).getThumbnail();
					MessageEmbed.VideoInfo videoInfo = originalMessage.getEmbeds().get(0).getVideoInfo();

					if (thumbnail != null) {
						if (videoInfo != null && videoInfo.getUrl() != null) {
							if (videoInfo.getUrl().contains("tenor")) {
								String gifLink = videoInfo.getUrl().replace("AAAPo", "AAAAd");
								builder.setImage(gifLink.replace(".mp4", ".gif"));
								builder.setDescription(originalMessage.getContentRaw().replace(url, ""));
							} else {
								builder.setImage(thumbnail.getUrl());
								builder.setDescription(originalMessage.getContentRaw());
							}
						}
					} else {
						builder.setDescription(originalMessage.getContentRaw());
					}
				}
			}
		} else {
			if (originalMessage.getAttachments().get(0).isImage()) {
				builder
					.setDescription(originalMessage.getContentRaw())
					.setImage(originalMessage.getAttachments().get(0).getUrl());
			} else {
				StringBuilder videoDesc = new StringBuilder()
					.append(originalMessage.getContentRaw())
					.append("\n\n")
					.append("***[Jump to video](")
					.append(originalMessage.getJumpUrl())
					.append(")***");

				builder
					.setDescription(videoDesc);
			}
		}

		// Check if the reactor has a yamboard profile
		if (!YamboardDB.checkForProfile(messageReactor.getIdLong()))
			YamboardDB.addYamboardProfile(messageReactor.getIdLong());

		return new Pair<>(
			postChannel.sendMessageEmbeds(builder.build())
				.setActionRow(getButtons(messageReactor)).complete().getIdLong(),
			builder
		);
	}

	private static List<Button> getButtons(Member messageReactor) {
		return Arrays.asList(
			Button.primary(
				messageReactor.getId() + ":" + new Upvote().getButtonId(),
				new Upvote().getEmoji()),
			Button.danger(
				messageReactor.getId() + ":" + new Downvote().getButtonId(),
				new Downvote().getEmoji())
		);
	}

	/**
	 * Check for url in a string
	 * @param message The message to check for a url
	 * @return The url if found, else empty
	 */
	private static String checkForUrl(String message) {
		Pattern pattern = Pattern.compile("([a-zA-Z0-9]+://)?([a-zA-Z0-9_]+:[a-zA-Z0-9_]+@)?([a-zA-Z0-9.-]+\\" +
			".[A-Za-z]{2,4})(:[0-9]+)?([^ ])+");
		Matcher matcher = pattern.matcher(message);
		String url = "";
		if (matcher.find())
		{
			url = matcher.group(0);
		}

		return url;
	}

	/**
	 * Count the number of reactions in a given list
	 * @param messageReactions The list of reactions on a message
	 * @return The number of reactions of a certain emoji in the list
	 */
	public static int countReactions(List<MessageReaction> messageReactions) {
		String emojiConfig = ConfigIO.readConfig("yamboard", "reactionemoji");
		if (emojiConfig == null) {
			logger.error("No reaction emoji found to count reactions");
			return 0;
		}

		int count = 0;
		for (MessageReaction messageReaction : messageReactions) {
			if (emojiConfig.equalsIgnoreCase(messageReaction.getEmoji().asUnicode().getAsCodepoints()))
				count++;
		}

		return count;
	}
}
