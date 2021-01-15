package com.reliquary.crow.commands.dnd.charactermanager.sheetsloader;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class SheetService {

	private static final String APPLICATION_NAME = "Crow Bot";

	/*
	authorize
	OAuth exchange to grant access to Google Sheets
	 */
	private static Credential authorize() throws IOException, GeneralSecurityException {

		// Load credentials.json
		InputStream in = SheetService.class.getResourceAsStream("/credentials.json");

		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
			JacksonFactory.getDefaultInstance(),
			new InputStreamReader(in)
		);

		// Scopes for access to spreadsheets
		List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);

		// Authorize and store token in file
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
			GoogleNetHttpTransport.newTrustedTransport(),
			JacksonFactory.getDefaultInstance(),
			clientSecrets,
			scopes)
			.setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
			.setAccessType("offline")
			.build();

		// Get credential object for access and return
		return new AuthorizationCodeInstalledApp(
			flow,
			new LocalServerReceiver())
			.authorize("user");
	}

	/*
	getSheetsService
	Obtains Sheets object by authorizing with API
	 */
	public static Sheets getSheetsService() throws IOException, GeneralSecurityException {

		// Get credential
		Credential credential = authorize();

		// Create new Sheets object and return
		return new Sheets.Builder(
			GoogleNetHttpTransport.newTrustedTransport(),
			JacksonFactory.getDefaultInstance(),
			credential)
			.setApplicationName(APPLICATION_NAME)
			.build();
	}

	/*
	Example stuff!

	// Obtain the sheets service to manipulate the spreadsheet
	// Remember to create a sheetsService object where you want to access data!
	Sheets sheetsService = SheetsAndJava.getSheetsService();
	String range = "congress!A2:F10"
		// 'congress' in this example is the sheet name
		// ! denotes a connection between the sheet and range
		// A2:F10 specifies the range of cells

	// Construct a response towards the sheets service
	// SPREADSHEET_ID for spreadsheet ID input
	ValueRange response = sheetsService.spreadsheets().values()
		.get(SPREADSHEET_ID, range)
		.execute();

	// Obtain a response
	List<List<Object>> values = response.getValues();

	// Check if there are values
	if (!((values == null) || (values.isEmpty())) {
		// Iterate through the list of values
		for (List row : values) {

		}
	} else
		logger.info("No values found in this range...");



	===========
	The first access will output a link to the console for OAuth
	===========
	 */
}
