package com.raikuman.troubleclub.club.utilities;

import com.raikuman.troubleclub.club.statemanager.CharacterStates;
import kotlin.Pair;
import net.dv8tion.jda.api.JDA;

import java.util.*;

/**
 * Singleton that holds all JDA information related to the respective characters
 *
 * @version 1.2 2023-19-02
 * @since 1.0
 */
public class JDAFinder {

	private static JDAFinder instance = null;
	private HashMap<CharacterNames, JDA> jdaMap;

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
	public void setJDAMap(HashMap<CharacterNames, JDA> jdaMap) {
		this.jdaMap = jdaMap;
	}

	/**
	 * Get the JDA given the key
	 * @param characterName The character name to get from the JDA map
	 * @return The pair of the key and JDA
	 */
	public JDA getJDA(CharacterNames characterName) {
		return jdaMap.get(characterName);
	}

	/**
	 * A list of character nicknames
	 * @param characterName The character to get nicknames from
	 * @return The list of character nicknames
	 */
	public static List<String> characterNicknames(CharacterNames characterName) {
		Map<CharacterNames, List<String>> characterMap = new LinkedHashMap<>() {{
			put(CharacterNames.INORI, Arrays.asList());
			put(CharacterNames.SUU, Arrays.asList());
			put(CharacterNames.DES, Arrays.asList());
			put(CharacterNames.CROW, Arrays.asList());
		}};

		return characterMap.get(characterName);
	}
}
