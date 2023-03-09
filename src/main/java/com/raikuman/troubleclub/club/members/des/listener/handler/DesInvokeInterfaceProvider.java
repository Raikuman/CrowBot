package com.raikuman.troubleclub.club.members.des.listener.handler;

import com.raikuman.botutilities.buttons.manager.ButtonInterface;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.troubleclub.club.members.des.commands.Karma;
import com.raikuman.troubleclub.club.members.des.commands.Yamboard;
import com.raikuman.troubleclub.club.members.des.yamboard.buttons.Downvote;
import com.raikuman.troubleclub.club.members.des.yamboard.buttons.Upvote;

import java.util.Arrays;
import java.util.List;

/**
 * Provides commands, buttons, selects, slashes, and modals for the ListenerHandler
 *
 * @version 1.0 2023-09-03
 * @since 1.0
 */
public class DesInvokeInterfaceProvider {

	/**
	 * Returns an array of commands
	 * @return The array of commands
	 */
	public static List<CommandInterface> provideCommands() {
		return Arrays.asList(
			new Yamboard(),
			new Karma()
		);
	}

	/**
	 * returns all button interfaces
	 * @return The list of button interfaces
	 */
	public static List<ButtonInterface> provideButtons() {
		return Arrays.asList(
			new Upvote(),
			new Downvote()
		);
	}
}
