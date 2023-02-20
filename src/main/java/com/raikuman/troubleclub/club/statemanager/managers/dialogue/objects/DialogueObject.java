package com.raikuman.troubleclub.club.statemanager.managers.dialogue.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raikuman.troubleclub.club.utilities.CharacterNames;

import java.util.List;

/**
 * Holds information for the dialogue
 *
 * @version 1.0 2023-18-01
 * @since 1.0
 */
public class DialogueObject {

	@JsonProperty("name")
	public String name;

	@JsonProperty("characters")
	public List<CharacterNames> involvedCharacters;

	@JsonProperty("voice-chat-allowed")
	public List<List<CharacterNames>> voiceChatCombinations;

	@JsonProperty("conversation")
	public List<DialogueCharacterObject> dialogueList;
}
