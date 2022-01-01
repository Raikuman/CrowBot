package com.reliquary.crow.resources.apis.googlesheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.reliquary.crow.resources.dnd.CharacterFetchInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * This class handles in providing methods to help to use and checking the SheetsProvider.java class
 *
 * @version 1.1 2021-31-12
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
	 * This method fetches information from the provided sheets link and range
	 * @param sheetsId Provides the full sheets link url
	 * @param range Provides the range to get values from the sheet
	 * @return Returns a string of the info fetched from the sheet
	 */
	public static String fetchInfo(String sheetsId, String range) {
		try {
			ValueRange response = SheetProvider.getInstance().sheetService.spreadsheets().values()
				.get(sheetsId, range)
				.execute();

			if (response.getValues() == null)
				return "";
			else
				return response.getValues().get(0).get(0).toString();
		} catch (IOException e) {
			logger.info(
				"Could not fetch data from id: [" + sheetsId + "] with range [" + range + "]"
			);
			return "";
		}
	}
}
