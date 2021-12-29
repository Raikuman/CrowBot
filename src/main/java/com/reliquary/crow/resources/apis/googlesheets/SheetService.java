package com.reliquary.crow.resources.apis.googlesheets;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * This class provides authentication to use the Google Sheets API and provides the Sheets object to get
 * data from spreadsheets
 *
 * @version 1.1 2021-28-12
 * @since 1.1
 */
public class SheetService {

	private static final Logger logger = LoggerFactory.getLogger(SheetService.class);
	private static final String APPLICATION_NAME = "Crow Bot";
	private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

	/**
	 * This method prompts OAuth2 authorization on initial run and returns a Credential object when authorized
	 * @return Returns the credential object
	 * @throws IOException Throws exception when credentials file is not found
	 * @throws GeneralSecurityException Throws exception when Google authorization fails
	 */
	private static Credential authorize() throws IOException, GeneralSecurityException {

		// Get credentials
		InputStream in = SheetService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		if (in == null) {
			throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
			GsonFactory.getDefaultInstance(),
			new InputStreamReader(in)
		);

		// Scopes to access spreadsheets
		List<String> scopes = List.of(
			SheetsScopes.SPREADSHEETS
		);

		// Authorize and store token in file
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
			GoogleNetHttpTransport.newTrustedTransport(),
			GsonFactory.getDefaultInstance(),
			clientSecrets,
			scopes
		).setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
			.setAccessType("offline")
			.build();

		// Get credential object for access and return
		return new AuthorizationCodeInstalledApp(
			flow,
			new LocalServerReceiver()
		).authorize("user");
	}

	/**
	 * This method gets the Sheets object using credentials in authorize()
	 * @return Returns Sheets object
	 */
	public static Sheets getSheetsService() {

		try {
			Credential credential = authorize();

			return new Sheets.Builder(
				GoogleNetHttpTransport.newTrustedTransport(),
				GsonFactory.getDefaultInstance(),
				credential)
				.setApplicationName(APPLICATION_NAME)
				.build();
		} catch (IOException | GeneralSecurityException e) {
			logger.info(e.getMessage());
		}

		// Service could not be created
		return null;
	}
}
