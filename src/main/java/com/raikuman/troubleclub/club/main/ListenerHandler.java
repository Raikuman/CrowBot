package com.raikuman.troubleclub.club.main;

import com.raikuman.botutilities.listener.ListenerBuilder;
import com.raikuman.botutilities.listener.ListenerManager;
import com.raikuman.troubleclub.club.members.suu.listener.handler.SuuInvokeInterfaceProvider;
import com.raikuman.troubleclub.club.members.des.yamboard.ReactionEventListener;
import com.raikuman.troubleclub.club.statemanager.managers.reply.ReplyEventListener;
import com.raikuman.troubleclub.club.utilities.CharacterNames;

import java.util.List;

/**
 * Handles creating a listener manager
 *
 * @version 1.1 2023-19-02
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
			.build();
	}

	public static ListenerManager getInoriListenerManager() {
		return new ListenerBuilder()
			.build();
	}

	public static ListenerManager getCrowListenerManager() {
		return new ListenerBuilder()
			.build();
	}

	public static ListenerManager getSuuListenerManager() {
		return new ListenerBuilder()
			.setListeners(List.of(new ReplyEventListener()))
			.setCommands(SuuInvokeInterfaceProvider.provideCommands())
			.setSlashes(SuuInvokeInterfaceProvider.provideSlashes())
			.setModals(SuuInvokeInterfaceProvider.provideModals())
			.build();
	}
}
