package com.raikuman.troubleclub.club.members.suu.listener.handler;

import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.modals.manager.ModalInterface;
import com.raikuman.botutilities.slashcommands.manager.SlashInterface;
import com.raikuman.troubleclub.club.members.suu.commands.other.*;
import com.raikuman.troubleclub.club.members.suu.commands.other.bot.*;
import com.raikuman.troubleclub.club.members.suu.commands.other.trello.RequestFeature;
import com.raikuman.troubleclub.club.members.suu.commands.other.trello.SubmitBug;

import java.util.Arrays;
import java.util.List;

/**
 * Provides commands, buttons, selects, slashes, and modals for the ListenerHandler
 *
 * @version 1.2 2023-22-02
 * @since 1.0
 */
public class SuuInvokeInterfaceProvider {

	/**
	 * Returns an array of commands
	 * @return The array of commands
	 */
	public static List<CommandInterface> provideCommands() {
		return Arrays.asList(
			new UpdateStateManager(),
			new Changelog(),
			new GetStickers(),
			new BotShutdown(),
			new RunDialogue(),
			new RunStatus(),
			new RunVoice(),
			new RunIdling(),
			new ShowStickers()
		);
	}

	/**
	 * Returns all slash interfaces
	 * @return The list of slash interfaces
	 */
	public static List<SlashInterface> provideSlashes() {
		return Arrays.asList(
			new RequestFeature(),
			new SubmitBug()
		);
	}

	/**
	 * Returns all modal interfaces
	 * @return The list of modal interfaces
	 */
	public static List<ModalInterface> provideModals() {
		return Arrays.asList(
			new RequestFeature(),
			new SubmitBug()
		);
	}
}
