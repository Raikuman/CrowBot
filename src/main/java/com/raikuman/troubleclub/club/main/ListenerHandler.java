package com.raikuman.troubleclub.club.main;

import com.raikuman.botutilities.listener.ListenerBuilder;
import com.raikuman.botutilities.listener.ListenerManager;

public class ListenerHandler {

	public static ListenerManager getListenerManager(String tokenVar) {
		switch (tokenVar) {
			case "destoken":
				return getDesListenerManager();

			case "inoritoken":
				return getInoriListenerManager();

			case "crowtoken":
				return getCrowListenerManager();

			case "suutoken":
				return getSuuListenerManager();
		}

		return null;
	}

	public static ListenerManager getDesListenerManager() {
		return new ListenerBuilder()
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
			.build();
	}
}
