package com.raikuman.troubleclub.club.members.des.yamboard;

import com.raikuman.botutilities.configs.EnvLoader;
import com.raikuman.botutilities.helpers.DateAndTime;
import com.raikuman.botutilities.helpers.RandomColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles the YamBoard, a pinned-message board based around reactions. A target text channel is selected
 * to look for reactions. The reacted message will then be posted to a pinned text channel.
 *
 * @version 1.0 2023-15-01
 * @since 1.0
 */
public class YamboardManager {

	private static final Logger logger = LoggerFactory.getLogger(YamboardManager.class);

	private final Map<Long, Long> messageIds = new LinkedHashMap<>();
	private static final String yamEmoji = "U+1f360";

	/**
	 * Handles the reaction event and keeps track of the map of message ids with posts
	 * @param event The generic message reaction event
	 */
	public void handleReaction(GenericMessageReactionEvent event) {
		// Check reaction
		String reactionUnicode = event.getEmoji().asUnicode().getAsCodepoints();

		if (event.getMember() == null)
			return;

		if (!yamEmoji.equalsIgnoreCase(reactionUnicode))
			return;

		// Get message and reactions
		Message message = event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete();
		int numReactions = countReactions(message.getReactions());

		Member messageMember = message.getMember();
		if (messageMember == null)
			return;

		TextChannel postChannel = event.getGuild().getTextChannelById(EnvLoader.get("yampostchannel"));
		if (postChannel == null)
			return;

		if (numReactions == 0) {
			postChannel.deleteMessageById(messageIds.get(event.getMessageIdLong())).queue();
			try {
				messageIds.remove(event.getMessageIdLong());
			} catch (NullPointerException e) {
				logger.info("Id not in map: " + event.getMessageIdLong());
			}
		} else if (numReactions > 0) {
			if (!messageIds.containsKey(event.getMessageIdLong()))
				addMessageId(
					event.getMessageIdLong(),
					postMessage(message, messageMember, event.getChannel().asTextChannel(), postChannel)
				);
		}
	}

	/**
	 * Posts a message to the pinned text channel given information from the original user message
	 * @param userMessage The message that was reacted to
	 * @param member The member associated with the original message
	 * @param userChannel The text channel where the reaction took place
	 * @param postChannel The text channel where the pinned message will be posted to
	 * @return The id long of the posted message
	 */
	private long postMessage(Message userMessage, Member member, TextChannel userChannel, TextChannel postChannel) {
		EmbedBuilder builder = new EmbedBuilder()
			.setAuthor(member.getEffectiveName(), null, member.getEffectiveAvatarUrl())
			.setColor(RandomColor.getRandomColor())
			.setFooter("#" + userChannel.getName() + " | " + DateAndTime.getDate() + " " + DateAndTime.getTime());

		String url = checkForUrl(userMessage.getContentRaw());
		if (userMessage.getAttachments().isEmpty()) {
			if (url.isEmpty()) {
				builder.setDescription(userMessage.getContentRaw());
			} else {
				if (!userMessage.getEmbeds().isEmpty()) {
					MessageEmbed.Thumbnail thumbnail = userMessage.getEmbeds().get(0).getThumbnail();
					MessageEmbed.VideoInfo videoInfo = userMessage.getEmbeds().get(0).getVideoInfo();

					if (thumbnail != null) {
						if (videoInfo != null && videoInfo.getUrl() != null) {
							if (videoInfo.getUrl().contains("tenor")) {
								String gifLink = videoInfo.getUrl().replace("AAAPo", "AAAAd");
								builder.setImage(gifLink.replace(".mp4", ".gif"));
								builder.setDescription(userMessage.getContentRaw().replace(url, ""));
							} else {
								builder.setImage(thumbnail.getUrl());
								builder.setDescription(userMessage.getContentRaw());
							}
						}
					} else {
						builder.setDescription(userMessage.getContentRaw());
					}
				}
			}
		} else {
			if (userMessage.getAttachments().get(0).isImage()) {
				builder
					.setDescription(userMessage.getContentRaw())
					.setImage(userMessage.getAttachments().get(0).getUrl());
			} else {
				StringBuilder videoDesc = new StringBuilder()
					.append(userMessage.getContentRaw())
					.append("\n\n")
					.append("***[Jump to video](")
					.append(userMessage.getJumpUrl())
					.append(")***");

				builder
					.setDescription(videoDesc);
			}
		}

		return postChannel.sendMessageEmbeds(builder.build()).complete().getIdLong();
	}

	/**
	 * Check for a url in a string
	 * @param message The message to check for a url
	 * @return The url if found, else empty
	 */
	private String checkForUrl(String message) {
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
	 * Puts message ids to the map with a limit of 10 entries
	 * @param userMessageId The reacted to message id long
	 * @param postMessageId The posted message id long
	 */
	private void addMessageId(long userMessageId, long postMessageId) {
		if (messageIds.size() < 10) {
			messageIds.put(userMessageId, postMessageId);
		} else {
			messageIds.remove(messageIds.keySet().iterator().next());
			messageIds.put(userMessageId, postMessageId);
		}
	}

	/**
	 * Count the number of reactions in a given list
	 * @param messageReactions The list of reactions on a message
	 * @return The number of reactions of a certain emoji in the list
	 */
	private int countReactions(List<MessageReaction> messageReactions) {
		int count = 0;
		for (MessageReaction messageReaction : messageReactions) {
			if (messageReaction.getEmoji().asUnicode().getAsCodepoints().equalsIgnoreCase(yamEmoji))
				count++;
		}

		return count;
	}
}
