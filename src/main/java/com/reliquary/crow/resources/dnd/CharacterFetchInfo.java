package com.reliquary.crow.resources.dnd;

import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.reliquary.crow.resources.apis.googlesheets.SheetProvider;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class provides all methods to get character information from the provided sheetsId
 *
 * @version 2.0 2022-12-01
 * @since 1.1
 */
public class CharacterFetchInfo {

	private final String sheetsId;

	public CharacterFetchInfo(String sheetsId) {
		this.sheetsId = sheetsId;
	}

	/**
	 * This method returns a hashmap of a cell range from Google Sheets
	 * @param paramList Provides the parameters to get from Google Sheets
	 * @return Returns a hashmap of the response
	 */
	public HashMap<String, List<List<String>>> fetchInfoCellRange(List<String> paramList) {

		List<String> ranges = new ArrayList<>();
		HashMap<String, List<List<String>>> characterMap = new HashMap<>();

		for (String param : paramList)
			if (getRangeMap().containsKey(param))
				ranges.add(getRangeMap().get(param));

		try {
			BatchGetValuesResponse response = SheetProvider.getInstance().sheetService.spreadsheets().values()
				.batchGet(sheetsId).setRanges(ranges).execute();

			int index = 0;
			for (ValueRange values : response.getValueRanges()) {

				// Default case
				if (values.getValues() == null) {
					characterMap.put(paramList.get(index), List.of(List.of("null")));
				} else {
					characterMap.put(paramList.get(index), values.getValues().stream().map(
						listObj -> listObj.stream().map(
							obj -> Objects.toString(obj, null)
						).collect(Collectors.toList())
					).collect(Collectors.toList()));
				}

				index++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return characterMap;
	}

	/**
	 * This method compresses the fetchInfoCellRange method so that the values are a single string
	 * @param paramList Provides the parameters to get from Google Sheets
	 * @return Returns a hashmap of the compressed response
	 */
	public HashMap<String, String> fetchInfoCell(List<String> paramList) {

		HashMap<String, String> characterMap = new HashMap<>();
		HashMap<String, List<List<String>>> cellRange = fetchInfoCellRange(paramList);

		for (Map.Entry<String, List<List<String>>> entry : cellRange.entrySet())
			if (entry.getValue().size() == 1)
				if (entry.getValue().get(0).size() == 1)
					characterMap.put(entry.getKey(), entry.getValue().get(0).get(0));

		return updateMapDefaults(characterMap);
	}

	/**
	 * This method sets the response data to default values if there was no data found
	 * @param characterMap Provides the hashmap to change defaults in
	 * @return Returns a hashmap of the updated response
	 */
	private HashMap<String, String> updateMapDefaults(HashMap<String, String> characterMap) {

		HashMap<String, String> updatedCharacterMap = new HashMap<>();

		for (Map.Entry<String, String> entry : characterMap.entrySet()) {
			if (entry.getValue().equals("null"))
				updatedCharacterMap.put(entry.getKey(), defaultMap().get(entry.getKey()));
			else
				updatedCharacterMap.put(entry.getKey(), entry.getValue());
		}

		return updatedCharacterMap;
	}

	/**
	 * This method returns the map of ranges to get from Google Sheets
	 * @return Returns a hashmap of ranges
	 */
	private HashMap<String, String> getRangeMap() {

		return new HashMap<>(Map.ofEntries(
			Map.entry("name", "v2.1!C6"),
			Map.entry("race", "v2.1!T7"),
			Map.entry("backstory", "v2.1!R165:R177"),
			Map.entry("portrait", "v2.1!C176"),
			Map.entry("alignment", "v2.1!AJ28"),
			Map.entry("level", "v2.1!AL6"),
			Map.entry("class", "v2.1!T5"),
			Map.entry("attacks1", "v2.1!AQ32:BC35"),
			Map.entry("attacks2", "Additional!AQ3:BC24")
		));
	}

	/**
	 * This method returns the map of defaults for keys
	 * @return Returns a hashmap of defaults
	 */
	private HashMap<String, String> defaultMap() {

		return new HashMap<>(Map.ofEntries(
			Map.entry("name", "Unnamed Character"),
			Map.entry("race", "Unknown"),
			Map.entry("backstory", ""),
			Map.entry("portrait", ""),
			Map.entry("alignment", "Unknown"),
			Map.entry("level", "Unknown"),
			Map.entry("class", "Unknown")
		));
	}
}
