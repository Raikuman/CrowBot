package com.reliquary.crow.resources.apis.googlesheets;

import java.util.HashMap;
import java.util.List;

/**
 * This class provides an object to easily get information from Google Sheets
 *
 * @version 1.0 2022-12-01
 * @since 1.1
 */
public class SheetDataFetcher {

	private final String sheetsId;
	private final HashMap<String, String> rangeMap, defaults;

	public SheetDataFetcher(String sheetsId, HashMap<String, String> rangeMap,
		HashMap<String, String> defaults) {
		this.sheetsId = sheetsId;
		this.rangeMap = rangeMap;
		this.defaults = defaults;
	}

	/**
	 * This method returns a hashmap of single cells
	 * @param paramList Provides the parameters to get from Google Sheets
	 * @return Returns a hashmap of the compressed response
	 */
	public HashMap<String, String> fetchCells(List<String> paramList) {

		return SheetResources.fetchInfoCell(sheetsId, rangeMap, defaults, paramList);
	}

	/**
	 * This method returns a hashmap of a range of cells
	 * @param paramList Provides the parameters to get from Google Sheets
	 * @return Returns a hashmap of the response
	 */
	public HashMap<String, List<List<String>>> fetchRanges(List<String> paramList) {

		return SheetResources.fetchInfoCellRange(sheetsId, rangeMap, paramList);
	}

}
