package com.reliquary.crow.resources.apis.googlesheets;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.reliquary.crow.resources.configs.envConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides authentication to use the Google Sheets API and provides the Sheets object to get
 * data from spreadsheets
 *
 * @version 2.0 2022-14-01
 * @since 1.1
 */
public class SheetService {

	private static final Logger logger = LoggerFactory.getLogger(SheetService.class);
	private static final String APPLICATION_NAME = "Crow_Bot";

	/**
	 * This method gets the Sheets object using API key
	 * @return Returns Sheets object
	 */
	public static Sheets getSheetsService() {

		HttpRequestInitializer httpRequestInitializer = request ->
			request.setInterceptor(intercepted ->
				intercepted.getUrl().set("key", envConfig.get("googlesheetsapikey"))
			);

		Sheets sheets = new Sheets.Builder(
			new NetHttpTransport.Builder().build(),
			GsonFactory.getDefaultInstance(),
			httpRequestInitializer
		).setApplicationName(APPLICATION_NAME).build();

		logger.info("Google Sheet API service created for application " + APPLICATION_NAME);

		return sheets;
	}
}
