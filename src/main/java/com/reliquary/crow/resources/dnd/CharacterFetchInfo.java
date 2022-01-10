package com.reliquary.crow.resources.dnd;

import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.reliquary.crow.resources.apis.googlesheets.SheetProvider;

import java.io.IOException;
import java.util.*;

/**
 * This class provides all methods to get character information from the provided sheetsId
 *
 * @version 1.2 2022-10-01
 * @since 1.1
 */
public class CharacterFetchInfo {

	private final String sheetsId;
	public LinkedHashMap<String, String> characterMap;

	public CharacterFetchInfo(String sheetsId) {
		this.sheetsId = sheetsId;

		characterMap = new LinkedHashMap<>(Map.of(
			"name", "v2.1!C6",
			"race", "v2.1!T7",
			"backstory", "v2.1!R165:R177",
			"portrait", "v2.1!C176",
			"alignment", "v2.1!AJ28",
			"level", "v2.1!AL6",
			"class", "v2.1!T5"
		));
	}

	/**
	 * This method batch fetches info from the Sheets and returns the response in a hashmap
	 * @param neededList Provides the list of items to fetch
	 * @return Returns a hashmap of the response
	 */
	public HashMap<String, String> batchFetchInfo(List<String> neededList) {

		List<String> ranges = new ArrayList<>();
		HashMap<String, String> valueMap = new LinkedHashMap<>();

		for (String needed : neededList) {
			if (characterMap.containsKey(needed)) {
				valueMap.put(needed, characterMap.get(needed));
				ranges.add(characterMap.get(needed));
			}
		}

		try {
			BatchGetValuesResponse response = SheetProvider.getInstance().sheetService.spreadsheets().values()
				.batchGet(sheetsId).setRanges(ranges).execute();

			int index = 0;
			StringBuilder stringBuilder;
			for (ValueRange range : response.getValueRanges()) {

				stringBuilder = new StringBuilder();

				if (range.getValues() == null) {
					stringBuilder.append("null");
				} else {
					for (List<Object> objectList : range.getValues()) {

						stringBuilder.append(objectList.get(0).toString());

						if (objectList.get(0).toString().endsWith("."))
							stringBuilder.append("\n");
						else
							stringBuilder.append(" ");
					}
				}

				valueMap.put(neededList.get(index), stringBuilder.toString());
				index++;
			}
		} catch (IOException e) {
			return null;
		}

		return updateMapDefaults(valueMap);
	}

	/**
	 * This method sets the response data to default values if there was no data found
	 * @param responseMap Provides the hashmap to change defaults in
	 * @return Returns a hashmap of the updated response
	 */
	private HashMap<String, String> updateMapDefaults(HashMap<String, String> responseMap) {

		HashMap<String, String> defaultMap = new HashMap<>(Map.of(
			"name", "Unnamed Character",
			"race", "Unknown",
			"backstory", "",
			"portrait", "",
			"alignment", "Unknown",
			"level", "Unknown",
			"class", "Unknown"
		));

		HashMap<String, String> outputMap = new HashMap<>();

		for (Map.Entry<String, String> entry : responseMap.entrySet()) {

			if (entry.getValue().equals("null"))
				outputMap.put(entry.getKey(), defaultMap.get(entry.getKey()));
			else
				outputMap.put(entry.getKey(), entry.getValue());
		}

		return outputMap;
	}
}
