package com.reliquary.crow.resources.apis.googlesheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.reliquary.crow.resources.dnd.CharacterFetchInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class handles in providing methods to help to use and checking the SheetsProvider.java class
 *
 * @version 1.2 2022-12-01
 * @since 1.1
 */
public class SheetResources {

	private static final Logger logger = LoggerFactory.getLogger(CharacterFetchInfo.class);

	/**
	 * This method converts the sheets link url to its id
	 * @param sheetsLink Provides the full sheets link url
	 * @return Returns the converted sheets link id
	 */
	public static String convertToId(String sheetsLink) {

		String[] list = sheetsLink.substring(39).split("/");

		if (list.length > 0)
			return list[0];

		return "";
	}

	/**
	 * This method checks if the sheetService instance can access the user's Google Sheet
	 * @param sheetsLink Provides the full sheets link url
	 * @return Returns boolean whether sheetService can access the sheet
	 */
	public static boolean checkUnlisted(String sheetsLink) {

		Sheets sheetService = SheetProvider.getInstance().sheetService;

		try {
			sheetService.spreadsheets().get(convertToId(sheetsLink)).execute();
			return true;
		} catch (IOException e) {
			logger.info(
				"Could not access sheet from id: [" + convertToId(sheetsLink) + "]"
			);
			return false;
		}
	}

	/**
	 * This method returns a hashmap of a cell range from Google Sheets
	 * @param sheetsId Provides the Google Sheets id
	 * @param rangeMap Provides the rangeMap to search for cells
	 * @param paramList Provides the parameters to get from Google Sheets
	 * @return Returns a hashmap of the response
	 */
	public static HashMap<String, List<List<String>>> fetchInfoCellRange(String sheetsId,
		HashMap<String, String> rangeMap, List<String> paramList) {

		List<String> ranges = new ArrayList<>();
		HashMap<String, List<List<String>>> characterMap = new HashMap<>();

		for (String param : paramList)
			if (rangeMap.containsKey(param))
				ranges.add(rangeMap.get(param));

		try {
			BatchGetValuesResponse response = SheetProvider.getInstance().sheetService.spreadsheets().values()
				.batchGet(sheetsId).setRanges(ranges).execute();

			int index = 0;
			for (ValueRange values : response.getValueRanges()) {
				// Set default cases
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
			logger.info(
				"Could not access information from sheet: [" + sheetsId + "]"
			);
		}

		return characterMap;
	}

	/**
	 * This method compresses the fetchInfoCellRange method so that the values are a single string
	 * @param sheetsId Provides the Google Sheets id
	 * @param rangeMap Provides the rangeMap to search for cells
	 * @param defaults Provides a default map to set default for empty values
	 * @param paramList Provides the parameters to get from Google Sheets
	 * @return Returns a hashmap of the compressed response
	 */
	public static HashMap<String, String> fetchInfoCell(String sheetsId, HashMap<String, String> rangeMap,
		HashMap<String, String> defaults, List<String> paramList) {

		HashMap<String, String> characterMap = new HashMap<>();
		HashMap<String, List<List<String>>> cellRange = fetchInfoCellRange(
			sheetsId,
			rangeMap,
			paramList
		);

		for (Map.Entry<String, List<List<String>>> entry : cellRange.entrySet())
			if (entry.getValue().size() == 1)
				if (entry.getValue().get(0).size() == 1)
					characterMap.put(entry.getKey(), entry.getValue().get(0).get(0));

		return updateMapDefaults(characterMap, defaults);
	}

	/**
	 *  This method sets the response data to default values if there was no data found
	 * @param characterMap Provides the hashmap to change defaults in
	 * @param defaults Provides a default map to set default for empty values
	 * @return Returns a hashmap of the updated response
	 */
	private static HashMap<String, String> updateMapDefaults(HashMap<String, String> characterMap,
		HashMap<String, String> defaults) {

		HashMap<String, String> updatedCharacterMap = new HashMap<>();

		for (Map.Entry<String, String> entry : characterMap.entrySet()) {
			if (entry.getValue().equals("null"))
				updatedCharacterMap.put(entry.getKey(), defaults.get(entry.getKey()));
			else
				updatedCharacterMap.put(entry.getKey(), entry.getValue());
		}

		return updatedCharacterMap;
	}
}
