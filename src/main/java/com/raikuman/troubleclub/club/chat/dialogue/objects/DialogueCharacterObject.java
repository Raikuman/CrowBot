package com.raikuman.troubleclub.club.chat.dialogue.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Holds information for the character of the dialogue
 *
 * @version 1.0 2023-18-01
 * @since 1.0
 */
public class DialogueCharacterObject {

	@JsonProperty("character")
	public String character;

	@JsonProperty("sticker")
	public boolean sticker;

	@JsonProperty("message")
	public String message;

	@JsonProperty("delay")
	public int delay;
}
