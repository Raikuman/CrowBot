package com.raikuman.troubleclub.club.statemanager.managers.dialogue.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raikuman.troubleclub.club.utilities.CharacterNames;

/**
 * Holds information for the character of the dialogue
 *
 * @version 1.0 2023-18-01
 * @since 1.0
 */
public class DialogueCharacterObject {

	@JsonProperty("character")
	public CharacterNames character;

	@JsonProperty("sticker")
	public boolean sticker;

	@JsonProperty("message")
	public String message;

	@JsonProperty("delay")
	public int delay;
}
