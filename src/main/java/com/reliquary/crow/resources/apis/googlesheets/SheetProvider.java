package com.reliquary.crow.resources.apis.googlesheets;

import com.google.api.services.sheets.v4.Sheets;

/**
 * This class is a singleton to provide the Sheets object so that authentication doesn't happen multiple times
 *
 * @version 1.0 2021-28-12
 * @since 1.1
 */
public class SheetProvider {

	public Sheets sheetService = SheetService.getSheetsService();
	private static final SheetProvider instance = new SheetProvider();

	private SheetProvider() {}

	/**
	 * This method returns the instance of the singleton
	 * @return Returns the SheetProvider instance
	 */
	public static SheetProvider getInstance() {
		return instance;
	}
}
