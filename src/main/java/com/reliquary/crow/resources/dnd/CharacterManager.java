package com.reliquary.crow.resources.dnd;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.reliquary.crow.resources.apis.googlesheets.SheetProvider;
import com.reliquary.crow.resources.apis.googlesheets.SheetResources;
import com.reliquary.crow.resources.configs.ConfigHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CharacterManager {

	private static final Logger logger = LoggerFactory.getLogger(CharacterManager.class);
	private static final String CHARACTER_DIRECTORY = ConfigHandler.loadConfigSetting("botSettings",
		"commandresourcedirectory") + "/dnd/";

	public static boolean addCharacter(String userId, String sheetsLink) {

		File userFolder = new File(CHARACTER_DIRECTORY + userId);

		// Create user folder
		if (userFolder.mkdir())
			logger.info("Created character folder for user: " + userFolder.getPath());
		else
			logger.info("Character folder for user already exists: " + userFolder.getPath());

		String[] list = userFolder.list();
		if (list == null)
			return false;

		String sheetId = SheetResources.convertToId(sheetsLink);

		String characterFileName = list.length + "=" + sheetId;

		File characterFile = new File(CHARACTER_DIRECTORY + userId + "/" + characterFileName);

		try {
			if (characterFile.createNewFile()) {
				logger.info("Character successfully created for user: " + userId + " | Sheets ID: " + sheetId);
				return true;
			}
		} catch (IOException e) {
			logger.info("Could not create character file for user: " + userId + " | Sheets ID: " + sheetId);
		}

		return false;
	}

	public static boolean deleteCharacter(String userId, int characterNum) {

		String target = null;
		for (String s : getCharacterList(userId)) {
			if (Integer.parseInt(s.split("")[0]) == (characterNum - 1)) {
				target = s;
				break;
			}
		}

		if (target == null)
			return false;

		File targetCharacter = new File(CHARACTER_DIRECTORY + userId + "/" + target);

		if (targetCharacter.exists()) {
			if (targetCharacter.delete()) {
				return setNewCharacter(userId);
			} else {
				return false;
			}
		}

		return false;
	}

	public static List<String> getCharacterList(String userId) {

		File userFolder = new File(CHARACTER_DIRECTORY + userId);

		if (!userFolder.exists())
			return new ArrayList<>();

		String[] list = userFolder.list();
		if (list == null)
			return new ArrayList<>();

		return new ArrayList<>(Arrays.asList(list));
	}

	public static boolean hasProfile(String userId) {
		return !getCharacterList(userId).isEmpty();
	}

	public static String getSheetId(String userId, int characterNum) {

		try {
			return getCharacterList(userId).get(characterNum - 1).substring(2);
		} catch (NullPointerException e) {
			return null;
		}
	}

	public static boolean validateCharacterLink(String sheetsLink) {

		final String copyright = "v2.1 sheet by I F Evans based on v1.3 sheet by DM David Lester based " +
			"on the official D&D character sheet available at Wizards.com/DnD.  " +
			"(TM & © 2014 Wizards of the Coast LLC.)";

		Sheets sheetService = SheetProvider.getInstance().sheetService;
		String range = "v2.1!B180";

		try {
			ValueRange response = sheetService.spreadsheets().values()
				.get(SheetResources.convertToId(sheetsLink), range)
				.execute();

			return response.getValues().get(0).get(0).equals(copyright);
		} catch (IOException e) {
			return false;
		}
	}

	public static boolean checkDuplicateCharacter(String userId, String sheetsLink) {

		for (String s : getCharacterList(userId))
			if (s.split("=")[1].equals(SheetResources.convertToId(sheetsLink)))
				return true;

		return false;
	}

	public static boolean deleteProfile(String userId) {

		File userFolder = new File(CHARACTER_DIRECTORY + userId);

		if (userFolder.exists()) {
			if (userFolder.delete()) {
				logger.info("Character folder deleted for user: " + userId);
				return true;
			}
			else {
				logger.info("An error occurred when deleting character folder for user: " + userId);
				return false;
			}
		}

		return false;
	}

	private static boolean setNewCharacter(String userId) {

		File newFile, oldFile;

		int characterNum = 0;
		for (String character : getCharacterList(userId)) {
			oldFile = new File(CHARACTER_DIRECTORY + userId + "/" + character);
			newFile = new File(CHARACTER_DIRECTORY + userId + "/" + characterNum + character.substring(1));

			characterNum++;

			if (!oldFile.renameTo(newFile))
				return false;
		}

		return true;
	}
}
