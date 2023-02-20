package com.raikuman.troubleclub.club.statemanager.managers.reply.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raikuman.troubleclub.club.utilities.CharacterNames;

import java.util.HashMap;
import java.util.List;

/**
 * Holds information for the reply
 *
 * @version 1.1 2023-21-01
 * @since 1.0
 */
public class ReplyObject {

	@JsonProperty("matches-needed")
	public int matchesNeeded;

	@JsonProperty("operation-type")
	public REPLY_OPERATION_TYPE operationType;

	@JsonProperty("characters")
	public HashMap<CharacterNames, ReplyCharacterObject> replyCharacterMap;

	@JsonProperty("match-words")
	public List<String> matchWords;
}
