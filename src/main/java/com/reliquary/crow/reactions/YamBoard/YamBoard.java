package com.reliquary.crow.reactions.YamBoard;

import com.reliquary.crow.reactions.manager.ReactContext;
import com.reliquary.crow.reactions.manager.ReactInterface;
import com.reliquary.crow.resources.jda.ReactionResources;
import com.reliquary.crow.resources.configs.ConfigHandler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Objects;

/**
 * This class handles the invocation of the yamboard, which requires two methods:
 * 1. When reaction is added, a yamboard must be sent to the target channel and ids saved to a file
 * 2. When reaction is removed, the yamboard tied to the message id must be removed and ids removed from file
 *
 * @version 1.0 2021-19-11
 * @since 1.0
 */
public class YamBoard implements ReactInterface {

	private final Logger logger = LoggerFactory.getLogger(YamBoard.class);

	@Override
	public void handleAdd(ReactContext ctx) {

		final TextChannel textChannel = ctx.getChannel();
		final User user = ctx.getMessage().getAuthor();
		final Message message = ctx.getMessage();

		// Check if react happens in the target channel
		if (!textChannel.getId().equalsIgnoreCase(
			ConfigHandler.loadConfigSetting("yamboardSettings", "chatTextChannel")))
			return;

		// Run react only if number of invokes is only 1
		if (ReactionResources.countReactions(message.getReactions(), getInvoke()) != 1)
			return;

		// Make sure the board file exists
		if (checkForFile())
			return;

		// Send embed
		try {
			TextChannel targetChannel = message.getGuild().getTextChannelById(
				Objects.requireNonNull(ConfigHandler.loadConfigSetting("yamboardSettings", "yamPostChannel"))
			);

			YamBoardResources yamBoardResources = new YamBoardResources();

			// Send embed and get its id
			assert targetChannel != null;
			String boardId = targetChannel.sendMessageEmbeds(
				yamBoardResources.buildEmbed(user, textChannel, message).build()
			).complete().getId();

			// Append ids to file
			yamBoardResources.appendBoardId(message.getId(), boardId, getBoardFile());
		} catch (NumberFormatException e) {
			logger.info("An error occurred when getting the target channel of the YamBoard");
		}
	}

	@Override
	public void handleRemove(ReactContext ctx) {

		final TextChannel textChannel = ctx.getChannel();
		final Message message = ctx.getMessage();

		// Check if react happens in the target channel
		if (!textChannel.getId().equalsIgnoreCase(
			ConfigHandler.loadConfigSetting("yamboardSettings", "chatTextChannel")))
			return;

		// Run react only if number of invokes is only 0
		if (ReactionResources.countReactions(message.getReactions(), getInvoke()) != 0)
			return;

		// Make sure the board file exists
		if (checkForFile())
			return;

		// Delete
		try {
			TextChannel targetChannel = message.getGuild().getTextChannelById(
				Objects.requireNonNull(ConfigHandler.loadConfigSetting("yamboardSettings", "yamPostChannel"))
			);

			YamBoardResources yamBoardResources = new YamBoardResources();

			// Delete message from channel
			assert targetChannel != null;
			targetChannel.deleteMessageById(Objects.requireNonNull(
				yamBoardResources.getBoardId(message.getId(), getBoardFile()))
			).queue();

			// Delete ids from file
			yamBoardResources.deleteBoardId(message.getId(), getBoardFile());

		} catch (NumberFormatException e) {
			logger.info("An error occurred when getting the target channel of the YamBoard");
		}
	}

	@Override
	public String getInvoke() {
		return ConfigHandler.loadConfigSetting("yamboardSettings", "yamboardEmoji");
	}

	@Override
	public String getCategory() {
		return "fun";
	}

	/**
	 * This method handles creating the yamboard file if it doesn't exist and return a boolean (it should
	 * return true at all times)
	 * @return Returns a boolean whether the file exists
	 */
	protected boolean checkForFile() {

		File boardFile = getBoardFile();

		if (!boardFile.exists()) {

			try {
				if (boardFile.createNewFile()) {
					logger.info("yamboard.txt created");
					return true;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * This method handles getting the yamboard file
	 * @return Returns the file object of the yamboard file
	 */
	protected File getBoardFile() {

		String boardLocation =
			ConfigHandler.loadConfigSetting("botSettings", "commandresourcedirectory") +
				"/" + "yamboard.txt";

		return new File(boardLocation);
	}
}
