package com.raikuman.troubleclub.club.chat.reply.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;

/**
 * Holds information for the reply
 *
 * @version 1.0 2023-18-01
 * @since 1.0
 */
public class ReplyObject {

	@JsonProperty("matches-needed")
	public int matchesNeeded;

	@JsonProperty("operation-type")
	public REPLY_OPERATION_TYPE operationType;

	@JsonProperty("operation-channel-id")
	public long operationChannelId;

	@JsonProperty("characters")
	public HashMap<String, ReplyCharacterObject> replyCharacterMap;

	@JsonProperty("match-words")
	public List<String> matchWords;
}
