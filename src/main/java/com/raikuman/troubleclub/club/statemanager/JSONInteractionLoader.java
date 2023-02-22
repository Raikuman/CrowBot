package com.raikuman.troubleclub.club.statemanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raikuman.troubleclub.club.statemanager.managers.dialogue.objects.DialogueObject;
import com.raikuman.troubleclub.club.statemanager.managers.reply.objects.ReplyObject;
import com.raikuman.troubleclub.club.statemanager.managers.status.objects.StatusCombinedArray;
import com.raikuman.troubleclub.club.statemanager.managers.status.objects.StatusObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Loads jsons for character bot interactions
 *
 * @version 1.0 2023-22-02
 * @since 1.0
 */
public class JSONInteractionLoader {

	public static final Logger logger = LoggerFactory.getLogger(JSONInteractionLoader.class);

	/**
	 * Loads a list of interaction objects that match the input class and returns it
	 * @param inputClass The requested input class to get the interaction type
	 * @param <T> The generic input class to get different interaction objects from
	 * @return The list of interaction objects that match the input class
	 */
	public static <T> List<T> loadInteractionObjects(Class<T> inputClass) {
		InteractionType interactionType = getInteractionFromClass(inputClass);
		if (interactionType == null) {
			logger.error("Could not get interaction type for " + inputClass);
			return null;
		}

		List<File> fileList = JSONInteractionLoader.getJsonFiles(interactionType);
		if (fileList == null) {
			logger.error("Could not load files for " + interactionType);
			return null;
		}

		List<T> objectList = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();
		for (File file : fileList) {
			try {
				objectList.add(objectMapper.readValue(file, inputClass));
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Could not read reply json for " + file);
			}
		}

		return objectList;
	}

	/**
	 * Get a list of all json files that match the interaction type given
	 * @param interactionType The interaction type to get files for
	 * @return The list of files that match the interaction type
	 */
	private static List<File> getJsonFiles(InteractionType interactionType) {
		File directory = new File("resources/" + interactionType.name());
		if (!directory.exists()) {
			if (!directory.mkdirs()) {
				logger.error("Could not create " + interactionType.name() + " directory");
				return null;
			}
		}

		if (!directory.isDirectory()) {
			logger.error("File " + interactionType.name() + " is not a directory");
			return null;
		}

		File[] directoryFiles = directory.listFiles();
		if (directoryFiles == null) {
			logger.error("Path or I/O error, could not load files from directory " + directory);
			return null;
		}

		// Check for jsons
		List<File> fileList = new ArrayList<>(Arrays.asList(directoryFiles));
		fileList.removeIf(file -> !file.getName().contains(".json"));

		return fileList;
	}

	/**
	 * Handles returning the correct interaction type that matches the requested input class
	 * @param inputClass The requested input class to get the interaction type
	 * @param <T> The generic input class to get different interaction types from
	 * @return The interaction type to match the input class
	 */
	private static <T> InteractionType getInteractionFromClass(Class<T> inputClass) {
		if (ReplyObject.class.equals(inputClass))
			return InteractionType.reply;
		else if (DialogueObject.class.equals(inputClass))
			return InteractionType.dialogue;
		else if (StatusObject.class.equals(inputClass))
			return InteractionType.status;
		else if (StatusCombinedArray.class.equals(inputClass))
			return InteractionType.statuscombined;

		return null;
	}
 }

/**
 * Interaction type enums
 *
 * @version 1.0 2023-22-02
 * @since 1.0
 */
enum InteractionType {
	reply, dialogue, status, statuscombined
}