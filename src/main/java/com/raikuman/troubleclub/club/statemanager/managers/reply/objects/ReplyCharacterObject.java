package com.raikuman.troubleclub.club.statemanager.managers.reply.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Holds information for the character of the reply
 *
 * @version 1.0 2023-18-01
 * @since 1.0
 */
public class ReplyCharacterObject {

	@JsonProperty("emoji")
	public String emoji;

	@JsonProperty("operation")
	public String operation;

	@JsonProperty("dialogue")
	public List<String> dialogue;

	@JsonProperty("unable")
	public List<String> unableDialogue;
}
