package com.reliquary.crow.resources.configs;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PresenceLoader {

	/*
	initializePresence
	Open new thread to change presence on a timer
	 */
	public void initializePresence() {

		Random rand = new Random();
		Timer timer = new Timer();

		timer.schedule(new TimerTask() {
			@Override
			public void run() {

			}
		}, 0, 5 * 60 * 1000);
	}

	private String loadFromPresence(String fileName) {



		// Placeholder
		return null;
	}
}
