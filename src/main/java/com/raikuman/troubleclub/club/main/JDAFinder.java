package com.raikuman.troubleclub.club.main;

import kotlin.Pair;
import net.dv8tion.jda.api.JDA;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Singleton that holds all JDA information related to the respective characters
 *
 * @version 1.0 2023-20-01
 * @since 1.0
 */
public class JDAFinder {

	private static JDAFinder instance = null;
	private HashMap<String, JDA> jdaMap;

	/**
	 * Return the current instance of JDAFinder, else create a new instance
	 * @return The JDAFinder instance
	 */
	public static JDAFinder getInstance() {
		if (instance == null)
			return new JDAFinder();

		return instance;
	}

	/**
	 * Set the map for JDAs
	 * @param jdaMap The map to set singleton JDAs to
	 */
	public void setJDAMap(HashMap<String, JDA> jdaMap) {
		this.jdaMap = jdaMap;
	}

	/**
	 * Get the JDA given the key
	 * @param key The key to get from the JDA map
	 * @return The pair of the key and JDA
	 */
	public Pair<String, JDA> getJDA(String key) {
		JDA jda = jdaMap.get(key);

		if (jda == null)
			return new Pair<>("", null);
		else
			return new Pair<>(key, jda);
	}

	/**
	 * A list of character names
	 * @return The list of character names
	 */
	public static List<String> characterNames() {
		return Arrays.asList(
			"inori",
			"suu",
			"des",
			"crow"
		);
	}
}
