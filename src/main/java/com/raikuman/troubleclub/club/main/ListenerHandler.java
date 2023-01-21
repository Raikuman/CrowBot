package com.raikuman.troubleclub.club.main;

import com.raikuman.botutilities.listener.ListenerBuilder;
import com.raikuman.botutilities.listener.ListenerManager;
import com.raikuman.troubleclub.club.chat.reply.ReplyEventListener;
import com.raikuman.troubleclub.club.members.suu.listener.handler.SuuInvokeInterfaceProvider;
import com.raikuman.troubleclub.club.members.des.yamboard.ReactionEventListener;

import java.util.List;

/**
 * Handles creating a listener manager
 *
 * @version 1.0 2023-18-01
 * @since 1.0
 */
public class ListenerHandler {

	/**
	 * Creates a listener manager given the bot variable
	 * @param botVar The bot variable to get the listener manager from
	 * @return The listener manager associated with the bot variable
	 */
	public static ListenerManager getListenerManager(String botVar) {
		switch (botVar) {
			case "des":
				return getDesListenerManager();

			case "inori":
				return getInoriListenerManager();

			case "crow":
				return getCrowListenerManager();

			case "suu":
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
