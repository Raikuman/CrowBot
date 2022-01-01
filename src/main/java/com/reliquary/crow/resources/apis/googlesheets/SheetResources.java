package com.reliquary.crow.resources.apis.googlesheets;

import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;

/**
 * This class handles in providing methods to help to use and checking the SheetsProvider.java class
 *
 * @version 1.0 2021-31-12
 * @since 1.1
 */
public class SheetResources {

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
			return false;
		}
	}
}
