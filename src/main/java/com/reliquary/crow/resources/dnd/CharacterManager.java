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

/**
 * This class manages creating, deleting, and adding characters to the user's profile under the
 * commandresourcedirectory to provide the Google Sheet link associated with that character
 *
 * @version 1.1 2021-31-12
 * @since 1.1
 */
public class CharacterManager {

	private static final Logger logger = LoggerFactory.getLogger(CharacterManager.class);
	private static final String CHARACTER_DIRECTORY = ConfigHandler.loadConfigSetting("botSettings",
		"commandresourcedirectory") + "/dnd/";

	/**
	 * This method adds a character using a user's provided Google Sheet link of their character sheet to
	 * their profile directory
	 * @param userId Provides the user's id
	 * @param sheetsLink Provides the Google Sheet link
	 * @return Returns a boolean whether the character was added or not
	 */
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

	/**
	 * This method deletes a character using the user's provided character number of their character from
	 * their profile directory
	 * @param userId Provides the user's id
	 * @param characterNum Provides the user's selected character number to delete
	 * @return Returns a boolean whether the character was deleted or not
	 */
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

	/**
	 * This method returns a list of all characteres under a user's profile directory
	 * @param userId Provides the user's id
	 * @return Returns the list of the user's character
	 */
	public static List<String> getCharacterList(String userId) {

		File userFolder = new File(CHARACTER_DIRECTORY + userId);

		if (!userFolder.exists())
			return new ArrayList<>();

		String[] list = userFolder.list();
		if (list == null)
			return new ArrayList<>();

		return new ArrayList<>(Arrays.asList(list));
	}

	/**
	 * This method checks if the user has a profile directory
	 * @param userId Provides the user's id
	 * @return Returns a boolean if the user has a profile
	 */
	public static boolean hasProfile(String userId) {
		return !getCharacterList(userId).isEmpty();
	}

	/**
	 * This method returns the sheetId of the selected character from the user's profile directory
	 * @param userId Provides the user's id
	 * @param characterNum Provides the user's selected character number to delete
	 * @return Returns the sheet id string
	 */
	public static String getSheetId(String userId, int characterNum) {

		try {
			return getCharacterList(userId).get(characterNum - 1).substring(2);
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * This method checks the Google Sheet link for the copyright to see if it is the correct character
	 * sheet to search for
	 * @param sheetsLink Provides the Google Sheet link
	 * @return Returns a boolean whether the sheet link is valid or not
	 */
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

	/**
	 * This method checks if the user's input sheet link is already a character that exists in the user's
	 * profile directory
	 * @param userId Provides the user's id
	 * @param sheetsLink Provides the Google Sheet link
	 * @return Returns a boolean if there is a duplicate character
	 */
	public static boolean checkDuplicateCharacter(String userId, String sheetsLink) {

		for (String s : getCharacterList(userId))
			if (s.split("=")[1].equals(SheetResources.convertToId(sheetsLink)))
				return true;

		return false;
	}

	/**
	 * This method deletes the user's profile directory
	 * @param userId Provides the user's id
	 * @return Returns a boolean if the user's profile directory was deleted or not
	 */
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

	/**
	 * This method switches the user's selected character with their new selected character
	 * @param userId Provides the user's id
	 * @param characterNum Provides the user's selected character number to delete
	 * @return Returns a boolean if the character was switched
	 */
	public static boolean switchCharacter(String userId, int characterNum) {

		File newFileCurrent, oldFileCurrent, newFileSelected, oldFileSelected;

		oldFileCurrent = new File(CHARACTER_DIRECTORY + userId + "/0=" + getSheetId(userId, 1));
		oldFileSelected =
			new File(CHARACTER_DIRECTORY + userId + "/" + (characterNum - 1) + "=" + getSheetId(userId,
				characterNum));

		int selectedNum;

		try {
			selectedNum = Integer.parseInt(oldFileSelected.getName().substring(0, 1));
		} catch (NumberFormatException e) {
			return false;
		}

		newFileCurrent =
			new File(CHARACTER_DIRECTORY + userId + "/" + selectedNum + "=" + oldFileCurrent.getName().substring(2));
		newFileSelected =
			new File(CHARACTER_DIRECTORY + userId + "/0=" + oldFileSelected.getName().substring(2));

		if (!oldFileCurrent.renameTo(newFileCurrent))
			return false;

		return oldFileSelected.renameTo(newFileSelected);
	}

	/**
	 * This method sets the selected character for the user
	 * @param userId Provides the user's id
	 * @return Returns a boolean if a new character was set
	 */
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
