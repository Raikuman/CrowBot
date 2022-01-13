package com.reliquary.crow.resources.dnd;

import java.util.*;

/**
 * This class provides all methods to get character information from the provided sheetsId
 *
 * @version 3.0 2022-12-01
 * @since 1.1
 */
public class CharacterFetchInfo {

	/**
	 * This method returns the map of ranges to get from Google Sheets
	 * @return Returns a hashmap of ranges
	 */
	public static HashMap<String, String> getRangeMap() {

		return new HashMap<>(Map.ofEntries(
			Map.entry("name", "v2.1!C6"),
			Map.entry("race", "v2.1!T7"),
			Map.entry("backstory", "v2.1!R165:R177"),
			Map.entry("portrait", "v2.1!C176"),
			Map.entry("alignment", "v2.1!AJ28"),
			Map.entry("level", "v2.1!AL6"),
			Map.entry("class", "v2.1!T5"),

			Map.entry("proficiencybonus", "v2.1!H14:I15"),
			Map.entry("weaponproficiencies", "v2.1!I50:N50"),
			Map.entry("armorclass", "v2.1!R12:S13"),
			Map.entry("initiative", "v2.1!V12:W13"),
			Map.entry("speed", "v2.1!Z12:AA13"),
			Map.entry("healthmax", "v2.1!U16:AA16"),
			Map.entry("healthcurrent", "v2.1!R17:AA18"),

			Map.entry("background", "v2.1!AJ11:AN11"),
			Map.entry("personalitytraits", "v2.1!AE12:AN14"),
			Map.entry("ideals", "v2.1!AE16:AN18"),
			Map.entry("bonds", "v2.1!AE20:AN22"),
			Map.entry("flaws", "v2.1!AE24:AN26"),

			Map.entry("age", "v2.1!C148:E148"),
			Map.entry("height", "v2.1!F148:H148"),
			Map.entry("weight", "v2.1!I148:K148"),
			Map.entry("size", "v2.1!L148:N148"),
			Map.entry("gender", "v2.1!C150:E150"),
			Map.entry("eyes", "v2.1!F150:H150"),
			Map.entry("hair", "v2.1!I150:K150"),
			Map.entry("skin", "v2.1!L150:N150"),

			Map.entry("attacks1", "v2.1!AQ32:BC35"),
			Map.entry("attacks2", "Additional!AQ3:BC24")
		));
	}

	/**
	 * This method returns the map of defaults for keys
	 * @return Returns a hashmap of defaults
	 */
	public static HashMap<String, String> defaultMap() {

		return new HashMap<>(Map.ofEntries(
			Map.entry("name", "Unnamed Character"),
			Map.entry("race", "Unknown"),
			Map.entry("portrait", ""),
			Map.entry("alignment", "Unknown"),
			Map.entry("level", "Unknown"),
			Map.entry("class", "Unknown"),

			Map.entry("proficiencybonus", "0"),
			Map.entry("weaponproficiencies", ""),
			Map.entry("armorclass", "0"),
			Map.entry("initiative", "0"),
			Map.entry("speed", "0 ft"),
			Map.entry("healthmax", "0"),
			Map.entry("healthcurrent", "0"),

			Map.entry("background", "Unknown"),
			Map.entry("age", "Unknown"),
			Map.entry("height", "Unknown"),
			Map.entry("weight", "Unknown"),
			Map.entry("size", "Unknown"),
			Map.entry("gender", "Unknown"),
			Map.entry("eyes", "Unknown"),
			Map.entry("hair", "Unknown0"),
			Map.entry("skin", "Unknown")
		));
	}
}
