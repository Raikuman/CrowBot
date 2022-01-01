package com.reliquary.crow.resources.dnd;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.reliquary.crow.resources.apis.googlesheets.SheetProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CharacterFetchInfo {

	private static final Logger logger = LoggerFactory.getLogger(CharacterFetchInfo.class);
	private static final Sheets sheetService = SheetProvider.getInstance().sheetService;

	private final String sheetsId;

	public CharacterFetchInfo(String sheetsId) {
		this.sheetsId = sheetsId;
	}

	public String characterName() {

		String characterName = fetchInfo("v2.1!C6");

		if (characterName.isEmpty())
			return "Unnamed Character";
		else
			return characterName;
	}

	public String characterPortrait() {

		String characterPortrait = fetchInfo("v2.1!C176");

		if (characterPortrait.isEmpty())
			return "";

		String fileId = characterPortrait.split("=")[characterPortrait.split("=").length-1];

		return "https://drive.google.com/uc?id=" + fileId;
	}

	private String fetchInfo(String range) {
		try {
			ValueRange response = sheetService.spreadsheets().values()
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
