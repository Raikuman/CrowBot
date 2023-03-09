package com.raikuman.troubleclub.club.main;

import com.raikuman.botutilities.listener.ListenerBuilder;
import com.raikuman.botutilities.listener.ListenerManager;
import com.raikuman.botutilities.slashcommands.manager.SlashInterface;
import com.raikuman.troubleclub.club.members.crow.listener.handler.CrowInvokeInterfaceProvider;
import com.raikuman.troubleclub.club.members.des.listener.handler.DesInvokeInterfaceProvider;
import com.raikuman.troubleclub.club.members.suu.commands.help.Help;
import com.raikuman.troubleclub.club.members.suu.listener.handler.SuuInvokeInterfaceProvider;
import com.raikuman.troubleclub.club.members.des.yamboard.ReactionEventListener;
import com.raikuman.troubleclub.club.utilities.CharacterNames;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles creating a listener manager
 *
 * @version 1.3 2023-09-03
 * @since 1.0
 */
public class ListenerHandler {

	/**
	 * Creates a listener manager given the bot variable
	 * @param characterName The character enum to get the listener manager from
	 * @return The listener manager associated with the bot variable
	 */
	public static ListenerManager getListenerManager(CharacterNames characterName) {
		switch (characterName) {
			case DES:
				return getDesListenerManager();

			case INORI:
				return getInoriListenerManager();

			case CROW:
				return getCrowListenerManager();

			case SUU:
				return getSuuListenerManager();
		}

		return null;
	}

	public static ListenerManager getDesListenerManager() {
		return new ListenerBuilder()
			.setListeners(List.of(new ReactionEventListener()))
			.setCommands(DesInvokeInterfaceProvider.provideCommands())
			.setButtons(DesInvokeInterfaceProvider.provideButtons())
			.build();
	}

	public static ListenerManager getInoriListenerManager() {
		return new ListenerBuilder()
			.build();
	}

	public static ListenerManager getCrowListenerManager() {
		return new ListenerBuilder()
			.setCommands(CrowInvokeInterfaceProvider.provideCommands())
			.build();
	}

	public static ListenerManager getSuuListenerManager() {
		List<SlashInterface> slashes = new ArrayList<>(SuuInvokeInterfaceProvider.provideSlashes());
		slashes.add(new Help());

		return new ListenerBuilder()
			.setListeners(SuuInvokeInterfaceProvider.provideListeners())
			.setCommands(SuuInvokeInterfaceProvider.provideCommands())
			.setButtons(SuuInvokeInterfaceProvider.provideButtons())
			.setSelects(SuuInvokeInterfaceProvider.provideSelects())
			.setSlashes(slashes)
			.setModals(SuuInvokeInterfaceProvider.provideModals())
			.build();
	}
}
