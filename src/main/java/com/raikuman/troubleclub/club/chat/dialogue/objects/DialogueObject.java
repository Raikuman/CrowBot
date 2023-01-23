package com.raikuman.troubleclub.club.chat.dialogue.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Holds information for the dialogue
 */
public class DialogueObject {

	@JsonProperty("conversation")
	public List<DialogueCharacterObject> dialogueList;
}
