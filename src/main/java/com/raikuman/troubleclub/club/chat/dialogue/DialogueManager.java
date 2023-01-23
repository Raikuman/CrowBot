package com.raikuman.troubleclub.club.chat.dialogue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raikuman.botutilities.configs.ConfigIO;
import com.raikuman.troubleclub.club.chat.ChatJSONLoader;
import com.raikuman.troubleclub.club.chat.dialogue.objects.DialogueCharacterObject;
import com.raikuman.troubleclub.club.chat.dialogue.objects.DialogueObject;
import com.raikuman.troubleclub.club.main.JDAFinder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Manages playing dialogue between bot characters
 *
 * @version 1.1 2023-20-01
 * @since 1.0
 */
public class DialogueManager {

	private static final Logger logger = LoggerFactory.getLogger(DialogueManager.class);

	private static DialogueManager instance = null;
	private HashMap<String, DialogueObject> dialogueObjectMap;

	public DialogueManager() {
		loadDialogueJsons();
	}

	public static DialogueManager getInstance() {
		if (instance == null)
			instance = new DialogueManager();

		return instance;
	}

	/**
	 * Load the dialogue data jsons to memory
	 */
	public void loadDialogueJsons() {
		File[] directoryFiles = ChatJSONLoader.getJsonFiles("dialogue");
		if (directoryFiles == null) {
			logger.error("Could not load files for replies");
			return;
		}

		// Get objects from jsons
		HashMap<String, DialogueObject> dialogueObjectMap = new LinkedHashMap<>();
		ObjectMapper objectMapper = new ObjectMapper();
		for (File file : directoryFiles) {
			try {
				dialogueObjectMap.put(
					file.getName(),
					objectMapper.readValue(file, DialogueObject.class)
				);
			} catch (IOException e) {
				logger.error("Could not read the reply json");
			}
		}

		this.dialogueObjectMap = dialogueObjectMap;
	}

	/**
	 * Plays dialogue between bots using dialogue objects
	 */
	public void playDialogue() {
		DialogueObject dialogueObject = getRandomDialogue();
		if (dialogueObject == null)
			return;

		for (DialogueCharacterObject dialogue : dialogueObject.dialogueList) {
			// Get target text channel for dialogue
			JDA currentJDA = JDAFinder.getInstance().getJDA(dialogue.character).getSecond();
			TextChannel textChannel = currentJDA.getTextChannelById(
				ConfigIO.readConfig("chat", "dialoguechannelid")
			);
			if (textChannel == null)
				return;

			// Handle stickers
			if (dialogue.sticker) {
				Guild guild = textChannel.getGuild();
				GuildSticker sticker = null;
				switch (dialogue.character) {
					case "crow":
						sticker = guild.getStickerById(ConfigIO.readConfig("chat", "crowsticker"));
						break;

					case "des":
						sticker = guild.getStickerById(ConfigIO.readConfig("chat", "dessticker"));
						break;

					case "suu":
						sticker = guild.getStickerById(ConfigIO.readConfig("chat", "suusticker"));
						break;

					case "inori":
						sticker = guild.getStickerById(ConfigIO.readConfig("chat", "inoristicker"));
						break;
				}

				if (sticker == null)
					continue;

				textChannel.sendStickers(sticker).completeAfter(dialogue.delay, TimeUnit.SECONDS);
				continue;
			}

			// Handle typing
			textChannel.sendTyping().complete();
			textChannel.sendMessage(dialogue.message).completeAfter(dialogue.delay, TimeUnit.SECONDS);
		}
	}

	/**
	 * Gets a random dialogue that has not been completed yet from the dialogue map
	 * @return The selected DialogueObject
	 */
	private DialogueObject getRandomDialogue() {
		// Append random dialogue to completed dialogue file
		File file = new File("resources/dialogue/completeddialogue.txt");
		if (!file.exists()) {
			try {
				if (!file.createNewFile())
					return null;

			} catch (IOException e) {
				logger.error("Could not create completed dialogue file");
				return null;
			}
		}

		// Get dialogue files already written in file
		List<String> completedDialogues;
		try {
			completedDialogues = Files.readAllLines(file.toPath(), Charset.defaultCharset());
		} catch (IOException e) {
			logger.error("Could not read lines from file");
			return null;
		}

		// Reset completed dialogues if all dialogues have been completed
		if (completedDialogues.size() == dialogueObjectMap.size()) {
			try(FileWriter fw = new FileWriter(file, false);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
				out.println("");
			} catch (IOException e) {
				logger.error("Could not print to file");
				return null;
			}

			completedDialogues = new ArrayList<>();
		}

		// Remove all completed dialogues from the current key list
		List<String> keyList = new ArrayList<>(dialogueObjectMap.keySet());
		for (String completed : completedDialogues)
			keyList.remove(completed);

		// Randomly select a key to get the dialogue from
		SecureRandom rand = new SecureRandom();
		String targetKey = keyList.get(rand.nextInt(keyList.size()));

		// Write key to file
		try(FileWriter fw = new FileWriter(file, true);
		    BufferedWriter bw = new BufferedWriter(fw);
		    PrintWriter out = new PrintWriter(bw))
		{
			out.println(targetKey);
		} catch (IOException e) {
			logger.error("Could not print to file");
			return null;
		}

		return dialogueObjectMap.get(targetKey);
	}
}
