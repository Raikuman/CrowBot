package com.reliquary.crow.resources.other;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles snipping a list of words into a list of sentences
 *
 * @version 1.0 2022-18-01
 * @since 1.1
 */
public class StringSnippet {

	/**
	 * Snips a list of single world strings into a list of strings based on how many characters should be
	 * on the string
	 * @param stringList Provides the list of single word strings
	 * @param charactersPerString Provides the number of characters that should be on each string
	 * @return Return a list of sentence strings
	 */
	public static List<String> snipString(List<String> stringList, int charactersPerString) {

		List<String> snipList = new ArrayList<>();
		StringBuilder stringBuilder = new StringBuilder();
		for (String string : stringList) {
			stringBuilder
				.append(string);

			if ((double) stringBuilder.toString().length() / charactersPerString > 1) {
				stringBuilder
					.append("...");

				snipList.add(stringBuilder.toString());
				stringBuilder = new StringBuilder();
			}

			stringBuilder
				.append(" ");
		}
		snipList.add(stringBuilder.toString());

		return snipList;
	}
}
