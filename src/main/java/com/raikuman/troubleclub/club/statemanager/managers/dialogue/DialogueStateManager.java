package com.raikuman.troubleclub.club.statemanager.managers.dialogue;

import com.raikuman.botutilities.configs.ConfigIO;
import com.raikuman.troubleclub.club.statemanager.CharacterStateManager;
import com.raikuman.troubleclub.club.statemanager.CharacterStateObject;
import com.raikuman.troubleclub.club.statemanager.JSONInteractionLoader;
import com.raikuman.troubleclub.club.statemanager.managers.dialogue.objects.DialogueCharacterObject;
import com.raikuman.troubleclub.club.statemanager.managers.dialogue.objects.DialogueObject;
import com.raikuman.troubleclub.club.statemanager.managers.voice.VoiceStateManager;
import com.raikuman.troubleclub.club.utilities.CharacterNames;
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
 * Manages playing the dialogue and updating dates to run the dialogue on
 *
 * @version 1.0 2023-19-02
 * @since 1.0
 */
public class DialogueStateManager {

	private static final Logger logger = LoggerFactory.getLogger(DialogueStateManager.class);

	private static DialogueStateManager instance = null;
	private List<DialogueObject> dialogueObjectList;

	public DialogueStateManager() {
		updateDialogueObjects();
	}

	public static DialogueStateManager getInstance() {
		if (instance == null)
			instance = new DialogueStateManager();

		return instance;
	}

	public void updateDialogueObjects() {
		this.dialogueObjectList = JSONInteractionLoader.loadInteractionObjects(DialogueObject.class);
	}

	/**
	 * Update and run the dialogue state
	 */
	public void updateDialogueState() {
		DialogueObject dialogueObject = getRandomDialogue();
		if (dialogueObject == null)
			return;

		for (DialogueCharacterObject dialogue : dialogueObject.dialogueList) {
			// Get target text channel for the character's JDA
			CharacterStateObject stateObject = CharacterStateManager.getInstance().getCharacterStateMap().get(dialogue.character);
			if (stateObject == null)
				return;

			TextChannel textChannel = stateObject.getJda().getTextChannelById(
				ConfigIO.readConfig("troubleclub/dialogue", "textchannelid")
			);
			if (textChannel == null)
				return;

			// Handle stickers
			if (dialogue.sticker) {
				GuildSticker guildSticker = getCharacterSticker(stateObject.getGuild(), dialogue.character);
				if (guildSticker == null)
					continue;

				textChannel.sendStickers(guildSticker).completeAfter(dialogue.delay, TimeUnit.SECONDS);
				continue;
			}

			// Handle typing
			textChannel.sendTyping().complete();
			textChannel.sendMessage(dialogue.message).completeAfter(dialogue.delay, TimeUnit.SECONDS);
		}

		handleVoiceChat(dialogueObject.involvedCharacters, dialogueObject.voiceChatCombinations);
	}

	/**
	 * Get a random dialogue from the dialogue list
	 * @return The selected dialogue
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

		// Get dialogues written in file
		List<String> completedDialogues;
		try {
			completedDialogues = Files.readAllLines(file.toPath(), Charset.defaultCharset());
		} catch (IOException e) {
			logger.error("Could not read lines from file");
			return null;
		}

		// Get overwrite config
		boolean overwriteDialogue = Boolean.parseBoolean(
			ConfigIO.readConfig("troubleclub/dialogue", "overwritepreviousdialogue")
		);

		// Reset completed dialogues if all dialogues have been completed
		if (overwriteDialogue && completedDialogues.size() == dialogueObjectList.size()) {
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
		} else if (!overwriteDialogue && completedDialogues.size() == dialogueObjectList.size()) {
			return null;
		}

		// Remove get only dialogues that haven't been completed
		List<DialogueObject> dialogueObjects = new ArrayList<>();
		for (DialogueObject dialogueObject : dialogueObjectList)
			if (!completedDialogues.contains(dialogueObject.name))
				dialogueObjects.add(dialogueObject);

		// Randomly select a dialogue object
		SecureRandom rand = new SecureRandom();
		DialogueObject dialogueObject = dialogueObjects.get(rand.nextInt(dialogueObjects.size()));

		// Write key to file
		try(FileWriter fw = new FileWriter(file, true);
		    BufferedWriter bw = new BufferedWriter(fw);
		    PrintWriter out = new PrintWriter(bw))
		{
			out.println(dialogueObject.name);
		} catch (IOException e) {
			logger.error("Could not print to file");
			return null;
		}

		return dialogueObject;
	}

	/**
	 * Handle the voice chat for the dialogue
	 * @param involvedCharacters The list of involved characters in the dialogue
	 * @param characterChatAllowed The list of combinations for allowed characters in the voice chat
	 */
	private void handleVoiceChat(List<CharacterNames> involvedCharacters, List<List<CharacterNames>> characterChatAllowed) {
		// Get all valid dialogue combinations from the voice channel
		List<List<CharacterNames>> foundCombinations = new ArrayList<>();
		for (List<CharacterNames> characterCombinations : characterChatAllowed) {
			int foundCharacters = 0;
			for (CharacterNames character : characterCombinations) {
				if (CharacterStateManager.getInstance().getCharacterStateMap()
					.get(character).getGuild().getAudioManager().isConnected())
					foundCharacters++;
			}

			if (foundCharacters == characterCombinations.size())
				foundCombinations.add(characterCombinations);
		}

		// Get the largest size of found combinations
		int largestListSize = 0;
		for (List<CharacterNames> characterCombinations : foundCombinations)
			if (characterCombinations.size() > 0)
				largestListSize = characterCombinations.size();

		// Remove all lists that are not the largest size
		for (List<CharacterNames> characterCombinations : foundCombinations)
			if (characterCombinations.size() != largestListSize)
				foundCombinations.remove(characterCombinations);

		// Randomly select a list of the largest size
		SecureRandom rand = new SecureRandom();
		List<CharacterNames> characterCombination = foundCombinations.get(rand.nextInt(foundCombinations.size()));

		// Remove involved characters from list to disconnect remaining characters
		characterCombination.removeAll(involvedCharacters);

		HashMap<CharacterNames, Boolean> connectionMap = new LinkedHashMap<>();
		for (CharacterNames characterName : characterCombination) {
			connectionMap.put(
				characterName,
				true
			);
		}

		VoiceStateManager.getInstance().updateConnections(connectionMap);
	}

	/**
	 * Get the character's sticker from the config
	 * @param guild The guild to get the sticker from
	 * @param characterName The character for the sticker
	 * @return The GuildSticker for dialogue use
	 */
	private GuildSticker getCharacterSticker(Guild guild, CharacterNames characterName) {
		switch (characterName) {
			case DES:
				return guild.getStickerById(ConfigIO.readConfig("troubleclub/dialogue", "dessticker"));

			case SUU:
				return guild.getStickerById(ConfigIO.readConfig("troubleclub/dialogue", "suusticker"));

			case CROW:
				return guild.getStickerById(ConfigIO.readConfig("troubleclub/dialogue", "crowsticker"));

			case INORI:
				return guild.getStickerById(ConfigIO.readConfig("troubleclub/dialogue", "inoristicker"));

			default:
				return null;
		}
	}
}
