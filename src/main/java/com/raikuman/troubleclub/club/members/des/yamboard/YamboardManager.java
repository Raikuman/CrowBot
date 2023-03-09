package com.raikuman.troubleclub.club.members.des.yamboard;

import com.raikuman.botutilities.configs.ConfigIO;
import com.raikuman.troubleclub.club.config.yamboard.YamboardDB;
import kotlin.Pair;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages handling yamboard posts
 *
 * @version 1.1 2023-09-03
 * @since 1.0
 */
public class YamboardManager {

	private static final Logger logger = LoggerFactory.getLogger(YamboardManager.class);

	private static YamboardManager instance = null;
	private final List<YamboardMessage> yamboardMessages = new ArrayList<>();

	public static YamboardManager getInstance() {
		if (instance == null)
			instance = new YamboardManager();

		return instance;
	}

	/**
	 * Handles the reaction event for creating or removing posts
	 * @param event The event to get yamboard post information from
	 */
	public void handleReaction(GenericMessageReactionEvent event) {
		// Check for event user
		if (event.getMember() == null)
			return;

		// Check for appropriate reaction
		String emojiConfig = ConfigIO.readConfig("yamboard", "reactionemoji");
		if (emojiConfig == null) {
			logger.error("No reaction emoji found for yamboard");
			return;
		}

		if (!emojiConfig.equalsIgnoreCase(event.getEmoji().asUnicode().getAsCodepoints()))
			return;

		// Get posting channel from config
		String postChannelId = ConfigIO.readConfig("yamboard", "postchannel");
		if (postChannelId == null)
			return;

		TextChannel postChannel = event.getGuild().getTextChannelById(postChannelId);
		if (postChannel == null)
			return;

		// Get message and reactions
		Message originalMessage = event.getChannel().retrieveMessageById(event.getMessageIdLong()).complete();
		if (originalMessage.getMember() == null)
			return;

		int numReactions = YamboardUtilities.countReactions(originalMessage.getReactions());
		if (numReactions == 0) {
			// No reactions, remove post
			YamboardMessage post = getMessageFromOriginal(event.getMessageIdLong());
			if (post == null)
				return;

			long originalPostId = post.getPostMessageId();
			if (originalPostId == -1L)
				return;

			removePost(originalPostId, postChannel);
		} else if (numReactions > 0) {
			// Reactions found, post if not already posted
			if (!checkPostExistsFromOriginal(event.getMessageIdLong())) {
				Pair<Long, EmbedBuilder> postMessageInfo = YamboardUtilities.postMessage(
					originalMessage,
					event.getMember(),
					event.getChannel().asTextChannel(),
					postChannel
				);

				if (postMessageInfo.getFirst() == -1L)
					return;

				addPost(
					event.getMember().getIdLong(),
					event.getMessageIdLong(),
					postMessageInfo
				);
			}
		}
	}

	/**
	 * Check if a post exists given a user's message
	 * @param originalMessageId The original user's message
	 * @return Whether if the user's message has a post
	 */
	public boolean checkPostExistsFromOriginal(long originalMessageId) {
		for (YamboardMessage post : yamboardMessages)
			if (post.getOriginalMessageId() == originalMessageId)
				return true;

		return false;
	}

	/**
	 * Add a post to the manager and update the user's yamboard entry
	 * @param posterId The user who reacted to the original message
	 * @param originalMessageId The original message's id
	 * @param postMessageInfo Information about the yamboard post
	 */
	public void addPost(long posterId, long originalMessageId, Pair<Long, EmbedBuilder> postMessageInfo) {
		YamboardMessage foundPost = null;
		for (YamboardMessage post : yamboardMessages) {
			if (post.getOriginalMessageId() == originalMessageId) {
				foundPost = post;
				break;
			}
		}

		if (foundPost != null)
			return;

		YamboardDB.handlePostNumber(posterId, true);
		yamboardMessages.add(new YamboardMessage(posterId, originalMessageId, postMessageInfo));
	}

	/**
	 * Remove a post from the manager and update the user's yamboard entry
	 * @param originalPostId The post to remove
	 * @param postChannel The channel where posts are posted
	 */
	public void removePost(long originalPostId, TextChannel postChannel) {
		for (YamboardMessage post : yamboardMessages) {
			if (post.getPostMessageId() == originalPostId) {
				postChannel.deleteMessageById(originalPostId).queue();
				YamboardDB.handlePostNumber(post.getPosterId(), false);

				if (post.getNumUpvoters() > 0)
					YamboardDB.manipulateKarma(post.getPosterId(), true, false, post.getNumUpvoters());

				if (post.getNumDownvoters() > 0)
					YamboardDB.manipulateKarma(post.getPosterId(), false, false, post.getNumDownvoters());

				yamboardMessages.remove(post);
				return;
			}
		}
	}

	/**
	 * Get the yamboard message object from the original message id
	 * @param originalMessageId The original message id to get the message object from
	 * @return The yamboard message object from the message id
	 */
	public YamboardMessage getMessageFromOriginal(long originalMessageId) {
		for (YamboardMessage post : yamboardMessages)
			if (post.getOriginalMessageId() == originalMessageId)
				return post;

		return null;
	}

	/**
	 * Get the yamboard message object from the post message id
	 * @param postMessageId The post message id to get the message object from
	 * @return The yamboard message object from the message id
	 */
	public YamboardMessage getMessageFromPost(long postMessageId) {
		for (YamboardMessage post : yamboardMessages)
			if (post.getPostMessageId() == postMessageId)
				return post;

		return null;
	}
}

/**
 * Holds information for the yamboard message
 *
 * @version 1.0 2023-07-03
 * @since 1.0
 */
class YamboardMessage {

	private final long posterId, originalMessageId, postMessageId;
	private final List<Long> upvoters, downvoters;
	private final EmbedBuilder postEmbed;

	public YamboardMessage(long posterId, long originalMessageId, Pair<Long, EmbedBuilder> postMessageInfo) {
		this.posterId = posterId;
		this.originalMessageId = originalMessageId;
		this.postMessageId = postMessageInfo.getFirst();
		this.postEmbed = postMessageInfo.getSecond();
		upvoters = new ArrayList<>();
		downvoters = new ArrayList<>();
	}

	public long getOriginalMessageId() {
		return originalMessageId;
	}

	public long getPostMessageId() {
		return postMessageId;
	}

	public long getPosterId() {
		return posterId;
	}

	public int getNumUpvoters() {
		return upvoters.size();
	}

	public int getNumDownvoters() {
		return downvoters.size();
	}

	/**
	 * Add an upvote to the post. If the voting user previously downvoted, it will be overturned with an
	 * upvote
	 * @param voterId The voter's id
	 */
	public void addUpvote(long voterId) {
		if (checkVoterStatus(voterId) == PostVoterStatus.DOWNVOTE) {
			downvoters.remove(voterId);
			YamboardDB.manipulateKarma(posterId, false, false, 1);
		}

		upvoters.add(voterId);
		YamboardDB.manipulateKarma(posterId, true, true, 1);
	}

	/**
	 * Remove an upvote from the post
	 * @param voterId The voter's id
	 */
	public void removeUpvote(long voterId) {
		if (checkVoterStatus(voterId) == PostVoterStatus.UPVOTE) {
			upvoters.remove(voterId);
			YamboardDB.manipulateKarma(posterId, true, false, 1);
		}
	}

	/**
	 * Add a downvote to the post. If the voting user previously upvoted, it will be overturned with an
	 * downvote
	 * @param voterId The voter's id
	 */
	public void addDownvote(long voterId) {
		if (checkVoterStatus(voterId) == PostVoterStatus.UPVOTE) {
			upvoters.remove(voterId);
			YamboardDB.manipulateKarma(posterId, true, false, 1);
		}

		downvoters.add(voterId);
		YamboardDB.manipulateKarma(posterId, false, true, 1);
	}

	/**
	 * Remove a downvote from the post
	 * @param voterId The voter's id
	 */
	public void removeDownvote(long voterId) {
		if (checkVoterStatus(voterId) == PostVoterStatus.DOWNVOTE) {
			downvoters.remove(voterId);
			YamboardDB.manipulateKarma(posterId, false, false, 1);
		}
	}

	/**
	 * Check if the input voter voted
	 * @param voterId The voter's id
	 * @param upvote Whether to check for an upvote or downvote
	 * @return Whether the voter voted
	 */
	public boolean checkVoterVoted(long voterId, boolean upvote) {
		List<Long> checkList;

		if (upvote)
			checkList = upvoters;
		else
			checkList = downvoters;

		for (Long voter : checkList) {
			if (voter == voterId)
				return true;
		}

		return false;
	}

	/**
	 * Check what type of vote the voter voted with
	 * @param voterId The voter's id
	 * @return The voter's voting status
	 */
	private PostVoterStatus checkVoterStatus(long voterId) {
		for (long voter : upvoters)
			if (voter == voterId)
				return PostVoterStatus.UPVOTE;

		for (long voter : downvoters)
			if (voter == voterId)
				return PostVoterStatus.DOWNVOTE;

		return PostVoterStatus.NONE;
	}

	/**
	 * Update the post embed with the post's new karma value
	 * @param event The event to update the embed with
	 */
	public void updateEmbed(ButtonInteractionEvent event) {
		List<MessageEmbed.Field> newFields = new ArrayList<>();
		for (MessageEmbed.Field embedField : postEmbed.getFields()) {
			if (embedField.getName() == null)
				continue;

			switch (embedField.getName()) {
				case "Upvotes":
					newFields.add(new MessageEmbed.Field(
						"Upvotes",
						String.valueOf(upvoters.size()),
						true
					));
					break;

				case "Downvotes":
					newFields.add(new MessageEmbed.Field(
						"Downvotes",
						String.valueOf(downvoters.size()),
						true
					));
					break;

				case "Ratio":
					DecimalFormat df = new DecimalFormat("#%");

					// Check for 0
					if (upvoters.size() + downvoters.size() == 0) {
						newFields.add(new MessageEmbed.Field(
							"Ratio",
							"-%",
							true
						));
					} else {
						newFields.add(new MessageEmbed.Field(
							"Ratio",
							df.format((double) (upvoters.size() / (upvoters.size() + downvoters.size()))),
							true
						));
					}
					break;
			}
		}

		postEmbed.clearFields();

		for (MessageEmbed.Field field : newFields)
			postEmbed.addField(field);

		event.editMessageEmbeds(postEmbed.build()).queue();
	}
}

/**
 * Enums for voter status
 *
 * @version 1.0 2023-07-03
 * @since 1.0
 */
enum PostVoterStatus {
	UPVOTE, DOWNVOTE, NONE
}
