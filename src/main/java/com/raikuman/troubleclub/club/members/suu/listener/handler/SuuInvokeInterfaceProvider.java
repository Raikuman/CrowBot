package com.raikuman.troubleclub.club.members.suu.listener.handler;

import com.raikuman.botutilities.modals.manager.ModalInterface;
import com.raikuman.botutilities.slashcommands.manager.SlashInterface;
import com.raikuman.troubleclub.club.members.suu.commands.other.trello.RequestFeature;
import com.raikuman.troubleclub.club.members.suu.commands.other.trello.SubmitBug;

import java.util.Arrays;
import java.util.List;

/**
 * Provides commands, buttons, selects, slashes, and modals for the ListenerHandler
 *
 * @version 1.0 2023-14-01
 * @since 1.0
 */
public class SuuInvokeInterfaceProvider {

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
