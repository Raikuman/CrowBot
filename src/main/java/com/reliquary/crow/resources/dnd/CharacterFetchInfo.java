package com.reliquary.crow.resources.dnd;

import com.reliquary.crow.resources.apis.googlesheets.SheetResources;

/**
 * This class provides all methods to get character information from the provided sheetsId
 *
 * @version 1.1 2021-31-12
 * @since 1.1
 */
public class CharacterFetchInfo {

	private final String sheetsId;

	public CharacterFetchInfo(String sheetsId) {
		this.sheetsId = sheetsId;
	}

	public String characterName() {

		String characterName = SheetResources.fetchInfo(sheetsId, "v2.1!C6");

		if (characterName.isEmpty())
			return "Unnamed Character";
		else
			return characterName;
	}

	public String characterPortrait() {

		String characterPortrait = SheetResources.fetchInfo(sheetsId, "v2.1!C176");

		if (characterPortrait.isEmpty())
			return "";

		String fileId = characterPortrait.split("=")[characterPortrait.split("=").length-1];

		return "https://drive.google.com/uc?id=" + fileId;
	}
}
