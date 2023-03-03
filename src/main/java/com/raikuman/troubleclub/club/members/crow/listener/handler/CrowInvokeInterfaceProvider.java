package com.raikuman.troubleclub.club.members.crow.listener.handler;

import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.troubleclub.club.members.crow.commands.roleplaying.Dice;

import java.util.Arrays;
import java.util.List;

/**
 * Provides commands, buttons, selects, slashes, and modals for the ListenerHandler
 *
 * @version 1.0 2023-02-03
 * @since 1.0
 */
public class CrowInvokeInterfaceProvider {

	/**
	 * Returns an array of commands
	 * @return The array of commands
	 */
	public static List<CommandInterface> provideCommands() {
		return Arrays.asList(
			new Dice()
		);
	}
}
