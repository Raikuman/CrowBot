package com.reliquary.crow.listeners;

import com.reliquary.crow.commands.fun.YamBoard;
import com.reliquary.crow.resources.configs.ConfigHandler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class ReactionChannelListener extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ReactionChannelListener.class);

	@Override
	public void onReady(@Nonnull ReadyEvent event) {
		logger.info("{} ReactionListener is initialized", event.getJDA().getSelfUser().getAsTag());
	}

	@Override
	public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {

		User user = event.getUser();
		Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
		MessageReaction reaction = event.getReaction();
		List<MessageReaction> reactions = message.getReactions();

		// Check if user is bot for bot commands
		if (user.isBot())
			return;

		// Yam Board Handler
		YamBoard yamBoard = new YamBoard();

		// Check if reaction is yam
		if (yamBoard.checkReaction(reaction)) {
			// Proceed if there was no yam reaction previously
			if ((yamBoard.countNumberOfReactions(reactions) - 1) == 0) {
				// Check the yam board if the message already exists
				if (!yamBoard.checkForId(message.getId())) {
					// Post board
					TextChannel postChannel = event.getGuild()
						.getTextChannelById(Objects.requireNonNull(ConfigHandler.loadConfigSetting(
							"yamboardSettings",
							"yamPostChannel")));

					// Append board ids
					assert postChannel != null;
					yamBoard.appendToYamBoard(
						message.getId(),
						postChannel.sendMessage(
							yamBoard.createYamBoardEmbed(message, event.getChannel().getName()).build())
							.complete().getId()
					);
				}
			}
		}
	}

	@Override
	public void onGuildMessageReactionRemove(@NotNull GuildMessageReactionRemoveEvent event) {

		User user = event.getUser();
		Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
		MessageReaction reaction = event.getReaction();
		List<MessageReaction> reactions = message.getReactions();

		// Check if user is bot for bot commands
		if (user.isBot())
			return;

		// Yam Board Handler
		YamBoard yamBoard = new YamBoard();

		// Check if reaction is yam
		if (yamBoard.checkReaction(reaction)) {
			// Proceed if there are no more reactions on the message
			if (yamBoard.countNumberOfReactions(reactions) == 0) {
				// Check if the yam board has the message
				if (yamBoard.checkForId(message.getId())) {
					// Delete board
					Objects.requireNonNull(
						event.getJDA().getTextChannelById(
							Objects.requireNonNull(
								ConfigHandler.loadConfigSetting(
									"yamboardSettings",
									"yamPostChannel"))))
						.deleteMessageById(yamBoard.getPostMessageId(message.getId())).queue();

					// Remove board ids
					yamBoard.removeFromYamBoard(message.getId());
				}
			}
		}
	}
}
