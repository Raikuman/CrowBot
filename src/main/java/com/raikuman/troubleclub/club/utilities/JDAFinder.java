package com.raikuman.troubleclub.club.utilities;

import kotlin.Pair;
import net.dv8tion.jda.api.JDA;

import java.util.*;

/**
 * Singleton that holds all JDA information related to the respective characters
 *
 * @version 1.1 2023-19-02
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
			instance = new JDAFinder();

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

	/**
	 * A list of character nicknames
	 * @param character The character to get nicknames from
	 * @return The list of character nicknames
	 */
	public static List<String> characterNicknames(String character) {
		Map<String, List<String>> characterMap = new LinkedHashMap<>() {{
			put("inori", Arrays.asList());
			put("suu", Arrays.asList());
			put("des", Arrays.asList());
			put("crow", Arrays.asList());
		}};

		return characterMap.get(character);
	}
}
